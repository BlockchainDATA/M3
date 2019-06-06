package eco.data.m3.demo.netperf.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import data.eco.m3.net.NetService;
import eco.data.m3.naming.client.NamingRestClient;
import eco.data.m3.naming.meta.PeerInfo;
import eco.data.m3.naming.meta.PeerRecord;
import eco.data.m3.net.android.NetServiceAndroid;
import eco.data.m3.net.core.MApiService;
import eco.data.m3.net.core.MId;
import eco.data.m3.net.p2p.PeerConfig;
import eco.data.m3.net.p2p.channel.PeerChannel;
import eco.data.m3.net.p2p.channel.PeerChannelListener;
import eco.data.m3.net.p2p.channel.PeerLink;
import eco.data.m3.net.p2p.channel.PeerLinkListener;
import eco.data.m3.net.p2p.channel.PeerLinkRole;
import eco.data.m3.net.p2p.channel.PeerLinkStatistic;
import eco.data.m3.net.p2p.message.NullMessage;
import eco.data.m3.net.p2p.message.PingMessage;
import eco.data.m3.net.p2p.message.handler.MessageHandler;
import io.netty.buffer.Unpooled;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A new connection app
 *
 * @author dai
 * @since 2019-03-11
 */
public class MainActivity extends AppCompatActivity implements PeerChannelListener, PeerLinkListener {
    private Context mContext;
    private List<LinkEntity> mDatas;
    private MyAdapter mAdapter;
    private ExecutorService threadPool;
    private boolean isConnection;

    public static final String WAITING = "Waiting";
    public static final String CONNECTED = "Connected";
    public static final String FAILED = "Failed";
    public static final String DISCONNECT = "Disconnect";

    //已连接的  mid  list
    private List<String> idList = new ArrayList<>();
    ApiService apiService;
    // list 请求到的peers
    List<PeerEntity> entities;
    // 可连接的peers
    List<PeerEntity> linkedPeers;


    ExecutorService singleThread = Executors.newFixedThreadPool(200);

    /**
     * 带宽测试 1
     * 延迟测试 2
     */
    private int type = 2;
    private String mid;
    NetService netService;
    String linkType = "rtc";
    /**
     * 发送的数据大小 默认1024
     */
    private byte[] mBytes = new byte[64 * 1024 - 15];

    /**
     * listPeers 的id和name Map
     */
    private Map<String, String> peersMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        initPeer();
    }

    private void initPeer() {
        linkedPeers = new ArrayList<>();
        mid = (String) SPUtils.get(mContext, "mid", "-1");
        if (mid.equals("-1")) {
            MId mId = new MId();
            mid = mId.toString();
            SPUtils.put(mContext, "mid", mid);
        }
        LogUtils.e("mid ---> " + mid);
        PeerInfo info = new PeerInfo(new MId(mid), Build.BRAND + " " + Build.MODEL,
                false);
        PeerConfig config = new PeerConfig();


        LogUtils.e("ip :  " + IPUtils.getIp());
        threadPool.submit(() -> {
            netService = new NetServiceAndroid(mContext, info, config);
            netService.addPeerChannelListener(MainActivity.this);
        });
    }

    private void initData() {
        mContext = this;
        mDatas = new ArrayList<>();
        mAdapter = new MyAdapter();
        threadPool = Executors.newCachedThreadPool();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MApiService.NamingBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
//        listPeers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.net_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.type_disconnect:
                showToast("开启断开重连测试,每5秒全部断开重连一次");
                break;
            case R.id.type_delay:
                type = 2;
                showToast("已选择延迟测试");
                break;
            case R.id.type_repeat:
                type = 1;
                showToast("已选带宽测试");
                break;
            case R.id.level_1:
                mBytes = new byte[1024 - 50];
                showToast("发送消息大小为1KB");
                break;
            case R.id.level_2:
                mBytes = new byte[1024 * 2 - 50];
                showToast("发送消息大小为2KB");
                break;
            case R.id.level_3:
                mBytes = new byte[1024 * 4 - 50];
                showToast("发送消息大小为4KB");
                break;
            case R.id.level_4:
                mBytes = new byte[1024 * 8 - 50];
                showToast("发送消息大小为8KB");
                break;
            case R.id.level_5:
                mBytes = new byte[1024 * 16 - 50];
                showToast("发送消息大小为16KB");
                break;
            case R.id.level_6:
                mBytes = new byte[1024 * 32 - 50];
                showToast("发送消息大小为32KB");
                break;
            case R.id.level_7:
                mBytes = new byte[1024 * 64 - 50];
                showToast("发送消息大小为64KB");
                break;
            case R.id.level_8:
                mBytes = new byte[1024 * 128 - 50];
                showToast("发送消息大小为128KB");
                break;
            case R.id.level_9:
                mBytes = new byte[1024 * 256 - 50];
                showToast("发送消息大小为256KB");
                break;
            case R.id.level_10:
                mBytes = new byte[1024 * 512 - 50];
                showToast("发送消息大小为512KB");
                break;
            case R.id.level_11:
                mBytes = new byte[1024 * 1024 - 50];
                showToast("发送消息大小为1MB");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    RecyclerView recyclerView;

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(String.format("netPerf-%s %s", Build.BRAND, Build.MODEL));
        recyclerView = findViewById(R.id.recycle_view);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,
                false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);
        mHandler.sendEmptyMessageDelayed(2, 1000);
        FloatingActionButton floatBtn = findViewById(R.id.float_btn);
        floatBtn.setOnClickListener(v -> {
            if (!isConnection) {
                isConnection = true;
                listPeers();
            } else {
                showToast("Connecting");
            }
        });
        RadioGroup radioGroup = findViewById(R.id.group);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_1_2_N:
                    //1对多测试
                    showToast("开启1对多测试");
                    testType = TYPE_1;
                    break;
                case R.id.rb_N_2_1:
                    //多对1测试
                    showToast("开启多对1测试");
                    testType = TYPE_N;
                    break;
            }
        });
    }

    public static final int TYPE_1 = 1;
    public static final int TYPE_N = 2;

    private int testType = TYPE_N;

    private Handler mHandler = new Handler(msg -> {
        switch (msg.what) {
            case 1:
                disconnect();
                listPeers();
                mAdapter.notifyDataSetChanged();
                this.mHandler.sendEmptyMessageDelayed(1, 5000);
                break;
            case 2:
                //固定每秒更新页面
                mAdapter.notifyDataSetChanged();
                this.mHandler.sendEmptyMessageDelayed(2, 1000);
                break;
        }
        return false;
    });

    private void requestConn(String mid) {
        LinkEntity linkingEntity = initEntity(mid);
        showToast("Start connecting");
        //正在连接的 link  实体
        threadPool.submit(() -> {
            PeerLink link = null;
            try {
//                link = netService.obtainLink(linkType, new MId(mid), false);
                link = netService.createLink(linkType, new MId(mid), true);
//                LogUtils.e("Mid  " + mid);
                //tcp方式连接
//                link = netService.obtainLink(new MId(mid));
                if (link == null) {
                    LogUtils.e("Create link failed");
                    return;
                }
                link.addListener(MainActivity.this);
                linkingEntity.setLink(link);
                linkingEntity.setState(WAITING);
                runOnUiThread(() -> mAdapter.notifyItemChanged(0));
                link.waitForOpen(20000);
                startTest(linkingEntity);
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showToast("Link fail");
                    linkingEntity.setFailedReason(e.getMessage());
                    linkingEntity.setState(FAILED);
                    mAdapter.notifyItemChanged(0);
                });
                e.printStackTrace();
                if (link != null)
                    netService.releaseLink(link, false);
            }
        });
    }

    boolean hasReply = false;

    /**
     * 计算延迟抖动 最大最小值 30秒内
     *
     * @param entity
     * @param count
     * @param delta
     */
    private List<Integer> deltaList = new ArrayList<>();
    int lastSum = 0;

    //TODO 算法待优化
    // 求最近30秒内的最大最小值，不足30秒（求不足的最大最小值）
    //超过30秒，（35秒，求最后30秒内最大最小值）
    private void getDelayTime(LinkEntity entity, int count, int delta) {
        LogUtils.e("count " + count);
        if (count == 1) {
            entity.setMin(delta);
            entity.setMax(delta);
        }
        lastSum += delta;
        deltaList.add(delta);

        if (count <= 30) {
            if (entity.getMax() < delta) {
                entity.setMax(delta);
            } else if (entity.getMin() > delta) {
                entity.setMin(delta);
            }
            entity.setDelay(lastSum / count);
        } else {
            lastSum = lastSum - deltaList.get(0);
            deltaList.remove(0);
            entity.setDelay(lastSum / 30);
            for (int i = 0; i < deltaList.size(); i++) {
                if (entity.getMax() < deltaList.get(i)) {
                    entity.setMax(deltaList.get(i));
                } else if (entity.getMin() > deltaList.get(i)) {
                    entity.setMin(deltaList.get(i));
                }
            }
        }
    }

    long bandStartTime;

    private void startTest(LinkEntity entity) {
        if (type == 2) {
            //延迟测试
            byte[] bytes = new byte[1024 - 15];
            singleThread.submit(() -> {
                PingMessage msg = new PingMessage(new String(bytes));
                for (int i = 0; i < 5; i++) {
                    if (!entity.getLink().isOpen()) {
                        return;
                    }
                    long start = System.currentTimeMillis();
                    hasReply = false;
                    try {
                        entity.getLink().sendMessage(msg, new MessageHandler() {
                            @Override
                            public void handle(PeerLink link, Object incoming) throws Throwable {
//                                PingMessage msg = (PingMessage) incoming;
                                long delta = System.currentTimeMillis() - start;
                                LogUtils.e("***** Get Ping Reply : " + delta + " ms ");
                                runOnUiThread(() -> {
                                    entity.setDeltaTime(delta);
                                    entity.getLink().getLogBuffer().append("\t").append(delta).append("ms");
                                    entity.setCount(entity.getCount() + 1);
                                    getDelayTime(entity, entity.getCount(), (int) entity.getDeltaTime());
                                    mAdapter.notifyDataSetChanged();
                                });
                                hasReply = true;
                            }

                            @Override
                            public void handleFault(PeerLink link, Object msg, String reason) {
                                runOnUiThread(() -> {
                                    entity.setDeltaTime(0);
                                    entity.getLink().getLogBuffer().append("\t").append(0).append("ms");
                                    entity.setCount(entity.getCount() + 1);
                                    getDelayTime(entity, entity.getCount(), (int) entity.getDeltaTime());
                                    mAdapter.notifyDataSetChanged();
                                });
                                hasReply = true;
                                LogUtils.e("***** Get Ping Reply Error ");
                            }
                        }, 5000);
                    } catch (Exception e) {
                        hasReply = false;
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            entity.setDeltaTime(0);
                            entity.getLink().getLogBuffer().append("\t").append(0).append("ms");
                            entity.setCount(entity.getCount() + 1);
                            getDelayTime(entity, entity.getCount(), (int) entity.getDeltaTime());
                            mAdapter.notifyDataSetChanged();
                        });
                    }
                    while (!hasReply) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                closeLink(entity);
            });
        } else {
            singleThread.submit(() -> {
                //带宽测试开始时间
                bandStartTime = System.currentTimeMillis();
                //持续5秒后关闭
                while (System.currentTimeMillis() - bandStartTime <= 3000000) {
                    if (!entity.getLink().isOpen()) {
                        return;
                    }
                    NullMessage msg = new NullMessage("", true);
                    msg.setBuffer(Unpooled.wrappedBuffer(mBytes));
                    try {
                        entity.getLink().sendMessage(msg);
                    } catch (Exception e) {
                        LogUtils.e("ex:  " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                closeLink(entity);
            });
        }
    }

    private LinkEntity initEntity(String mid) {
        PeerEntity data = null;
        LogUtils.e("linkedPeers " + linkedPeers.size());
        // 你这里对于序号i没有什么用
        for (PeerEntity entity : linkedPeers) {
            if (entity.getInfo().getNodeId().equals(mid)) {
                data = entity;
                break;
            }
        }

        idList.add(mid);
        LinkEntity entity = new LinkEntity();
        entity.setRemoteId(mid);
        entity.setType(type);
        entity.setName(data != null ? data.getInfo().getName() : "无");
        entity.setAddress(data != null ? data.getInfo().getAddress() : "无");
        entity.setTime("--");
        entity.setState(WAITING);
        entity.setCount(0);
        entity.setLink(null);
        mDatas.add(0, entity);
        mAdapter.notifyItemInserted(0);
        recyclerView.smoothScrollToPosition(0);
        return entity;
    }

    /**
     * 断开全部
     */
    private void disconnect() {
        for (LinkEntity entity : mDatas) {
            if (entity.getLink() != null) {
                entity.getLink().removeListener(this);
                entity.setState(DISCONNECT);
                netService.releaseLink(entity.getLink(), false);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void listPeers() {
        Call<List<PeerEntity>> list = apiService.list();
        list.enqueue(new Callback<List<PeerEntity>>() {
            @Override
            public void onResponse(Call<List<PeerEntity>> call, Response<List<PeerEntity>> response) {
                isConnection = false;
                entities = response.body();
                LogUtils.e("list peers : " + response.body());
//                showToast("找到" + entities.size() + "个节点");
                linkedPeers.clear();
                for (PeerEntity entity : entities) {
                    if (!entity.getInfo().getNodeId().equals(mid) &&
                            !entity.getInfo().isSuperNode() &&
                            entity.getInfo().isEnableP2P()) {
                        linkedPeers.add(entity);
                    }
                }
                if (testType == TYPE_1) {
                    connAll();
                } else {
                    connOne();
                }

//                peersMap.clear();
//                peersMap.put("全部连接", "0");
//                linkedPeers.clear();
//                if (entities.size() > 1) {
//                    //正常连接
//                    for (PeerEntity entity : entities) {
//                        if (!entity.getInfo().getNodeId().equals(mid) &&
//                                !entity.getInfo().isSuperNode() &&
//                                entity.getInfo().isEnableP2P()) {
//                            linkedPeers.add(entity);
//                            peersMap.put(entity.getInfo().getName(), entity.getInfo().getNodeId());
//                        }
//                    }
//                }
//                if (isFirst) {
//                    isFirst = false;
//                    return;
//                }
//                if (peersMap.size() > 1) {
//                    showPeerDialog();
//                } else {
//                    showToast("未找到可用节点，稍后再试。");
//                }
            }

            @Override
            public void onFailure(Call<List<PeerEntity>> call, Throwable t) {
                LogUtils.e("list peers error " + t.getMessage());
                showToast("查找错误");
                isConnection = false;
            }
        });
    }


    //重连
//    private void reconnected(LinkEntity entity, String mid) {
//        long startTime = System.currentTimeMillis();
//        showToast("Reconnected..");
//        threadPool.submit(() -> {
//            try {
//                PeerLink link = netService.obtainLink(linkType, MId.fromHexString(mid));
//                link.addListener(this);
//                runOnUiThread(() -> {
//                    entity.setLink(link);
//                    entity.setState(link != null ? CONNECTED : FAILED);
//                    entity.setTime(System.currentTimeMillis() - startTime + "ms");
//                    mAdapter.notifyItemChanged(mDatas.indexOf(entity));
//                });
//                startTest(entity);
//            } catch (Exception e) {
//                showToast("Link fail");
//                entity.setState(FAILED);
//                e.printStackTrace();
//            }
//        });
//    }

    private void showPeerDialog() {
        String[] peerNames = new String[peersMap.size()];
        Set<String> keys = peersMap.keySet();
        Iterator<String> iterator = keys.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            String next = iterator.next();
            peerNames[count++] = next;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Peer found");
        builder.setSingleChoiceItems(peerNames, -1, (dialog, which) -> {
            dialog.cancel();
            if (peerNames[which].equals("全部连接")) {
                connAll();
            } else {
                requestConn(peersMap.get(peerNames[which]));
            }
        });
        builder.show();
    }

    /**
     * 全部连接
     */
    private void connAll() {
        for (PeerEntity entity : linkedPeers) {
            if (!idList.contains(entity.getInfo().getNodeId())) {
                requestConn(entity.getInfo().getNodeId());
            }
        }
    }

    /**
     * 连接指定节点
     */
    private void connOne() {
        //华为 pic-al00 的mid
        for (int i = 0; i < 20; i++) {
            requestConn("EzfzfWAc4cfINbIfktr1mn_bEDY");
        }

    }

    /**
     * 收到一条连接
     */
    private void addEntity(String id, PeerLink link, PeerRecord record) {
        showToast("Receive link");
        idList.add(id);
        LinkEntity entity = new LinkEntity();
        entity.setRemoteId(id);
        entity.setTime("-");
        entity.setName(record != null ? record.getInfo().getName() : "无");
        entity.setAddress(record != null ? record.getInfo().getAddress().getHostString() : "无");
        entity.setState(WAITING);
        entity.setLink(link);
        entity.setCount(0);
        mDatas.add(0, entity);
        mAdapter.notifyItemInserted(0);
        recyclerView.smoothScrollToPosition(0);

//        if (!idList.contains(id)) {
//            LinkEntity entity = new LinkEntity();
//            entity.setRemoteId(id);
//            entity.setTime("-");
//            entity.setName(record != null ? record.getInfo().getName() : "无");
//            entity.setAddress(record != null ? record.getInfo().getAddress().getHostString() : "无");
//            entity.setState(CONNECTED);
//            entity.setLink(link);
//            entity.setCount(0);
//            mDatas.add(0, entity);
//            mAdapter.notifyItemInserted(0);
//        } else {
//            for (int i = 0; i < mDatas.size(); i++) {
//                if (mDatas.get(i).getLink() == link) {
//                    mDatas.get(i).setState(CONNECTED);
//                    mDatas.get(i).setCount(0);
//                    mAdapter.notifyItemChanged(i);
//                    break;
//                }
//            }
//        }
    }

    @Override
    public void onPeerLinkRemoved(PeerChannel channel, PeerLink link) {
        LogUtils.d("Link Remove : " + link);

        link.removeListener(this);
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).getLink() == link) {
                mDatas.get(i).setState(DISCONNECT);
            }
        }
        runOnUiThread(() -> {

            long duration = System.currentTimeMillis() - bandStartTime;
            double bandwidth = link.getStatistic().getOutBytes() / (duration / 1000.0);
            link.getLogBuffer().append("平均带宽").append(bandwidth / (1 << 20)).append("MB/s");
//                    entity.setCount(entity.getCount() + 1);
//                    getDelayTime(entity, entity.getCount(), (int) entity.getDeltaTime());
//            mAdapter.notifyDataSetChanged();
//                    long bandByte = mBytes.length * entity.getCount() / 30;
//                    showToast(String.format("平均每秒发送%d字节", bandByte));

            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onPeerLinkOpen(PeerLink link) {
        LogUtils.d("Link Open : " + link);
        LogUtils.d(link.getLogBuffer().toString());

        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).getLink() == link) {
                mDatas.get(i).setState(CONNECTED);
            }
        }
        runOnUiThread(() ->
                mAdapter.notifyDataSetChanged());
    }

    @Override
    public void onPeerLinkClose(PeerLink link) {
        LogUtils.d("Link Close : " + link);
    }

    @Override
    public void onPeerLinkExceptionCaught(PeerLink link, Exception e) {
        LogUtils.d("Link Exception : " + link + "\n" + e);
    }

    @Override
    public void onPeerLinkAdded(PeerChannel channel, PeerLink link) {
        LogUtils.d("Link Add : " + link);
        link.addListener(this);
        NamingRestClient client = new NamingRestClient();
        PeerRecord peer = client.getPeer(link.getRemoteNodeId());
        if (link.getRole() == PeerLinkRole.Acceptor) {
            runOnUiThread(() -> {
                addEntity(link.getRemoteNodeId().toString(), link, peer);
            });
        }
    }

    private class MyAdapter extends RecyclerView.Adapter {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_main, viewGroup,
                    false);
            return new MyViewHolder(inflate);
        }

        @SuppressLint({"SetTextI18n", "DefaultLocale"})
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MyViewHolder viewHolder = (MyViewHolder) holder;

            LinkEntity entity = mDatas.get(position);
            viewHolder.idTv.setText(String.format("Id: %s", entity.getRemoteId()));
            viewHolder.stateTv.setText(String.format("状态: %s", entity.getState()));
            viewHolder.logBtn.setVisibility(View.GONE);
            if (entity.getLink() != null) {
                viewHolder.logBtn.setVisibility(View.VISIBLE);
                PeerLink link = entity.getLink();
                PeerLinkStatistic statistic = link.getStatistic();
                if (entity.getLink().getRole() == PeerLinkRole.Acceptor) {
                    viewHolder.roleTv.setText("Role: 接收方");
                } else if (entity.getLink().getRole() == PeerLinkRole.Connector) {
                    viewHolder.roleTv.setText("Role: 发送方");
                } else {
                    viewHolder.roleTv.setText("Role: None");
                }

                //新增抖动延迟和平均延迟
                viewHolder.delayTv.setText(String.format("30秒内平均延迟: %d ms", entity.getDelay()));

                viewHolder.shakeTv.setText(String.format("抖动延迟(最大值、最小值): %d ms, %d ms",
                        entity.getMax() - entity.getDelay(), entity.getMin() - entity.getDelay()));

                //   当前时间 - （连接创建时间+连接时间）
                // = 当前时间 - 连接成功时间
                long now = System.currentTimeMillis();
                long untilTime = link.getRemoveTime() > 0 ? Math.min(now, link.getRemoveTime()) : now;
                double totalSendTime = (untilTime - link.getStatistic().getConnectTime()) / 1000.0;

                viewHolder.linkTv.setText(String.format("连接(%s - %d) : 引用数 %d",
                        link.getPeerChannel().getName(), link.getRemoteSessionId(), link.getRefCnt()));
                viewHolder.nameTv.setText(String.format("连接设备: %s", entity.getName()));
                viewHolder.addressTv.setText(String.format("连接IP: %s", entity.getAddress()));
                if (type == 2) {
                    if (entity.getLink().getRole() == PeerLinkRole.Acceptor)
                        viewHolder.countOutTv.setText(String.format("发送数目、字节:(%d,\t%dB)",
                                statistic.getOutCnt(), statistic.getOutBytes()));
                    else {
                        viewHolder.countOutTv.setText(String.format("发送数目、字节、延迟:(%d,\t%dB,  %s)",
                                statistic.getOutCnt(), statistic.getOutBytes(),
                                entity.getDeltaTime() >= 0 ? entity.getDeltaTime() + "ms" : "超时"));
                    }
                } else {
                    viewHolder.countOutTv.setText(String.format("发送:(%d报文,\t%dB)",
                            statistic.getOutCnt(), statistic.getOutBytes()));
                }
                if (!entity.getState().equals(DISCONNECT) && !entity.getState().equals(FAILED)) {
                    viewHolder.timeTv.setText(String.format("连接用时: %d ms", statistic.getConnectTime() > statistic.getCreateTime() ?
                            statistic.getConnectTime() - statistic.getCreateTime() : now - statistic.getCreateTime()));
                    viewHolder.createTimeTv.setText(String.format("连接时长(自连接成功起): %d s",
                            statistic.getConnectTime() > 0 ? ((System.currentTimeMillis() - statistic.getConnectTime()) / 1000) : 0));
                }
                viewHolder.countInTv.setText(String.format("接收:(%d报文,\t%dB)",
                        statistic.getInCnt(), statistic.getInBytes()));

                viewHolder.speedInTv.setText(String.format("接收速度:(%.2f报文/s,\t%.2fKB/s)",
                        statistic.getSpeedInCnt(), statistic.getSpeedInBytes() / 1024));
                viewHolder.speedInAvgTv.setText(String.format("平均接收速度(%.2f报文/s,\t%.2fKB/s)",
                        statistic.getInCnt() / totalSendTime, statistic.getInBytes() / (totalSendTime * 1024)));

                viewHolder.speedOutTv.setText(String.format("发送速度:(%.2f报文/s,\t%.2fKB/s)",
                        statistic.getSpeedOutCnt(), statistic.getSpeedOutBytes() / 1024));
                viewHolder.speedOutAvgTv.setText(String.format("平均发送速度(%.2f报文/s,\t%.2fKB/s)",
                        statistic.getOutCnt() / totalSendTime, statistic.getOutBytes() / (totalSendTime * 1024)));

                viewHolder.descTv.setText(entity.getFailedReason());
            }
            // init  act con
            if (entity.getLink() != null) {
                if (entity.getLink().getRole() == eco.data.m3.net.p2p.channel.PeerLinkRole.None) {
                    viewHolder.typeView.setBackgroundColor(Color.parseColor("#2A63FF"));//blue
                } else if (entity.getLink().getRole() == PeerLinkRole.Acceptor) {
                    viewHolder.typeView.setBackgroundColor(Color.parseColor("#5EE44B"));// green
                } else {
                    viewHolder.typeView.setBackgroundColor(Color.parseColor("#FFB700"));//red
                }
            } else {
                viewHolder.typeView.setBackgroundColor(Color.parseColor("#2A63FF"));//blue
            }

            viewHolder.itemView.setOnClickListener(v -> {
//                if (entity.getState().equals(CONNECTED)) {
//                    closeLink(entity);
//                }
//                else if (mDatas.get(position).getState().equals(DISCONNECT)) {
//                    reconnected(mDatas.get(position), position);
//                }
            });
            viewHolder.itemView.setOnLongClickListener(v -> {
                if (entity.getState().equals(CONNECTED)) {
                    closeLink(entity);
                }
                return false;
            });
            viewHolder.logBtn.setOnClickListener(v -> {
                LogDialog dialog = new LogDialog();
                dialog.setContent(entity.getLink().getLogBuffer().toString());
                dialog.show(getSupportFragmentManager(), "2");
            });
        }

        //重连
//        private void reconnected(LinkEntity entity, int pos) {
//            long startTime = System.currentTimeMillis();
//            showToast("Reconnected..");
//            threadPool.submit(() -> {
//                try {
//                    PeerLink link = netService.obtainLink(linkType, MId.fromHexString(entity.getRemoteId()));
//                    link.addListener(MainActivity.this);
//                    runOnUiThread(() -> {
//                        entity.setLink(link);
//                        entity.setState(link != null ? CONNECTED : FAILED);
//                        entity.setTime(System.currentTimeMillis() - startTime + "ms");
//                        mAdapter.notifyItemChanged(pos);
//                    });
//                    if (link != null) {
//                        startTest(entity);
//                    }
//                } catch (Exception e) {
//                    showToast("Link fail");
//                    entity.setState(FAILED);
//                    mAdapter.notifyItemChanged(pos);
//                    e.printStackTrace();
//                }
//            });
//        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView nameTv, timeTv, createTimeTv, speedInTv, speedOutTv, shakeTv,
                    speedInAvgTv, speedOutAvgTv, stateTv, descTv, delayTv,
                    countInTv, countOutTv, idTv, addressTv, roleTv, linkTv;
            View typeView, itemView;
            Button logBtn;

            MyViewHolder(@NonNull View itemView) {
                super(itemView);
                this.itemView = itemView;
                logBtn = itemView.findViewById(R.id.logBtn);
                idTv = itemView.findViewById(R.id.item_id);
                linkTv = itemView.findViewById(R.id.item_link);
                roleTv = itemView.findViewById(R.id.item_role);
                addressTv = itemView.findViewById(R.id.item_address);
                nameTv = itemView.findViewById(R.id.item_name);
                timeTv = itemView.findViewById(R.id.item_time);
                createTimeTv = itemView.findViewById(R.id.item_create_time);
                countInTv = itemView.findViewById(R.id.item_count_in);
                countOutTv = itemView.findViewById(R.id.item_count_out);
                speedInTv = itemView.findViewById(R.id.item_speed_in);
                speedInAvgTv = itemView.findViewById(R.id.item_speed_in_avg);
                speedOutTv = itemView.findViewById(R.id.item_speed_out);
                speedOutAvgTv = itemView.findViewById(R.id.item_speed_out_avg);
                stateTv = itemView.findViewById(R.id.item_state);
                descTv = itemView.findViewById(R.id.item_desc);
                typeView = itemView.findViewById(R.id.item_type);
                delayTv = itemView.findViewById(R.id.item_delay);
                shakeTv = itemView.findViewById(R.id.item_shake);
            }
        }
    }

    private void closeLink(LinkEntity entity) {
        if (entity.getLink() != null) {
            entity.getLink().removeListener(this);
            netService.releaseLink(entity.getLink(), false);
        }

        runOnUiThread(() -> {
            showToast("Disconnecting..");
            entity.setState(DISCONNECT);
            mAdapter.notifyDataSetChanged();

            //断开之后请求重连
            requestConn("EzfzfWAc4cfINbIfktr1mn_bEDY");
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        scheduledExecutorService.shutdownNow();
        threadPool.shutdownNow();
        singleThread.shutdown();
        mHandler.removeMessages(1);
        mHandler.removeMessages(2);
    }

    private void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
}
