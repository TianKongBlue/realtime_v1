import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bw.realtime.base.BaseApp;
import com.bw.realtime.bean.CartAddUuBean;
import com.bw.realtime.constant.Constant;
import com.bw.realtime.function.DorisMapFunction;
import com.bw.realtime.util.DateFormatUtil;
import com.bw.realtime.util.FlinkSinkUtil;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;


import java.time.Duration;

public class DwsTradeCartAddUuWindow extends BaseApp {
    public static void main(String[] args) {
        new DwsTradeCartAddUuWindow().start(Constant.TOPIC_DWD_TRADE_CART_ADD,"1",1,10026);
    }
    @Override
    public void handle(StreamExecutionEnvironment env, DataStreamSource<String> stream) {
        stream
            .map(JSON::parseObject)
            .assignTimestampsAndWatermarks(
                WatermarkStrategy
                    .<JSONObject>forBoundedOutOfOrderness(Duration.ofSeconds(5L))
                    .withTimestampAssigner((obj, ts) -> obj.getLong("ts") * 1000)
                    .withIdleness(Duration.ofSeconds(120L))

            )
            .keyBy(obj -> obj.getString("user_id"))
            .process(new KeyedProcessFunction<String, JSONObject, CartAddUuBean>() {

                private ValueState<String> lastCartAddDateState;

                @Override
                public void open(Configuration parameters) {
                    lastCartAddDateState = getRuntimeContext().getState(new ValueStateDescriptor<String>("lastCartAddDate", String.class));
                }

                @Override
                public void processElement(JSONObject jsonObj,
                                           Context context,
                                           Collector<CartAddUuBean> out) throws Exception {
                    String lastCartAddDate = lastCartAddDateState.value();
                    long ts = jsonObj.getLong("ts") * 1000;
                    String today = DateFormatUtil.tsToDate(ts);

                    if (!today.equals(lastCartAddDate)) {
                        lastCartAddDateState.update(today);

                        out.collect(new CartAddUuBean("", "", "", 1L));
                    }

                }
            })
            .windowAll(TumblingEventTimeWindows.of(Time.seconds(5L)))
            .reduce(
                new ReduceFunction<CartAddUuBean>() {
                    @Override
                    public CartAddUuBean reduce(CartAddUuBean value1,
                                                CartAddUuBean value2) {
                        value1.setCartAddUuCt(value1.getCartAddUuCt() + value2.getCartAddUuCt());
                        return value1;
                    }
                },
                new ProcessAllWindowFunction<CartAddUuBean, CartAddUuBean, TimeWindow>() {
                    @Override
                    public void process(Context ctx,
                                        Iterable<CartAddUuBean> elements,
                                        Collector<CartAddUuBean> out) throws Exception {
                        CartAddUuBean bean = elements.iterator().next();
                        bean.setStt(DateFormatUtil.tsToDateTime(ctx.window().getStart()));
                        bean.setEdt(DateFormatUtil.tsToDateTime(ctx.window().getEnd()));
                        bean.setCurDate(DateFormatUtil.tsToDateForPartition(System.currentTimeMillis()));

                        out.collect(bean);
                    }
                }
            ).map(new DorisMapFunction<>()).print();

//            .sinkTo(FlinkSinkUtil.getDorisSink(Constant.DWS_TRADE_CART_ADD_UU_WINDOW));


    }
}
