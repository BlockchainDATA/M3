package eco.data.m3.demo.netperf.android;

/**
 * author: dai
 * date:   $date$
 * des:
 */
public class PeerEntity {

    /**
     * info : {"nodeId":"7ef75e73532033588636779a513d904ca2292ab2","name":"google Android SDK built for x86","directAddress":null,"address":null,"data":null,"superNode":false}
     * lastUpdateTime : 2019-03-13T08:37:12.960+0000
     * registTime : 2019-03-13T08:37:12.958+0000
     * unRegistTime : null
     */

    private InfoBean info;
    private String lastUpdateTime;
    private String registTime;
    private Object unRegistTime;

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getRegistTime() {
        return registTime;
    }

    public void setRegistTime(String registTime) {
        this.registTime = registTime;
    }

    public Object getUnRegistTime() {
        return unRegistTime;
    }

    public void setUnRegistTime(Object unRegistTime) {
        this.unRegistTime = unRegistTime;
    }

    public static class InfoBean {
        /**
         * nodeId : 7ef75e73532033588636779a513d904ca2292ab2
         * name : google Android SDK built for x86
         * directAddress : null
         * address : null
         * data : null
         * superNode : false
         */

        private String nodeId;
        private String name;
        private String directAddress;
        private String address;
        private Object data;
        private boolean enableP2P;

        public boolean isEnableP2P() {
            return enableP2P;
        }

        public void setEnableP2P(boolean enableP2P) {
            this.enableP2P = enableP2P;
        }

        private boolean superNode;

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDirectAddress() {
            return directAddress;
        }

        public void setDirectAddress(String directAddress) {
            this.directAddress = directAddress;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public boolean isSuperNode() {
            return superNode;
        }

        public void setSuperNode(boolean superNode) {
            this.superNode = superNode;
        }
    }
}
