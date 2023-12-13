package fr.isep;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZookeeperClient {
    private CuratorFramework client;
    private LeaderLatch leaderLatch;
    private String nodeId;

    public ZookeeperClient(String connectionString, String nodeId) {
        this.nodeId = nodeId;
        client = CuratorFrameworkFactory.newClient(connectionString, new ExponentialBackoffRetry(1000, 3));
        client.start();

        leaderLatch = new LeaderLatch(client, "/leader-election", nodeId);
        try {
            leaderLatch.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isLeader() {
        try {
            return leaderLatch.hasLeadership();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setNumber(int number) {
        try {
            String path = "/game/number";
            byte[] numberBytes = String.valueOf(number).getBytes();
            if (client.checkExists().forPath(path) != null) {
                client.setData().forPath(path, numberBytes);
            } else {
                client.create().creatingParentsIfNeeded().forPath(path, numberBytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNumber() {
        try {
            String path = "/game/number";
            if (client.checkExists().forPath(path) != null) {
                byte[] numberBytes = client.getData().forPath(path);
                return Integer.parseInt(new String(numberBytes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // 默认值或错误值
    }

    // 其他方法...
}
