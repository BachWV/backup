package backup;

import java.io.*;
import java.util.*;

/**
 * Huffman 压缩算法实现
 */
public class HuffmanCompressor {

    public void compress(File source, File target) throws IOException {
        try (InputStream in = new BufferedInputStream(new FileInputStream(source));
             DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(target)))) {
            // 统计各个 byte 出现的次数
            Map<Byte, Integer> byteToCount = getByteToCount(source);
            // 根据各个 byte 出现的次数建立 Huffman 树
            Node root = buildHuffmanTree(byteToCount);
            // 根据 Huffman 树得到每个 byte 的编码
            Map<Byte, String> encodingMap = getEncodingMap(root);
            // 将编码表写入压缩后的文件头部，解码时要用到
            writeEncodingMap(encodingMap, out);
            // 计算压缩后的文件有多少 bit，写入该长度
            writeBitLength(out, byteToCount, encodingMap);
            // 开始压缩文件
            encode(in, out, encodingMap);
        }
    }

    public void decompress(File source, File target) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(source)));
             OutputStream out = new BufferedOutputStream(new FileOutputStream(target))) {
            // 读取编码表
            Map<String, Byte> decodingMap = readDecodingMap(in);
            // 读取压缩后的数据的长度
            long totalBitLength = in.readLong();
            // 开始解码
            decode(in, out, decodingMap, totalBitLength);
        }
    }

    private void encode(InputStream in, DataOutputStream out, Map<Byte, String> encodingMap) throws IOException {
        int[] bits = new int[8];
        int n = 0;
        int b = in.read();
        while (b != -1) {
            String encoding = encodingMap.get((byte) b);
            assert encoding != null;

            for (char ch : encoding.toCharArray()) {
                int bit = ch - '0';
                bits[n++] = bit;
                if (n == 8) {
                    out.writeByte(bitsToByte(bits));
                    n = 0;
                }
            }

            b = in.read();
        }

        if (n > 0) {
            out.writeByte(bitsToByte(bits));
        }
    }

    private void decode(DataInputStream in, OutputStream out, Map<String, Byte> decodingMap, long totalBitLength) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte bits = 0;
        for (int i = 0; i < totalBitLength; i++) {
            if (i % 8 == 0) {
                bits = in.readByte();
            }
            int position = 7 - i % 8;
            int bit = (bits >> position) & 1;
            sb.append(bit);
            if (decodingMap.containsKey(sb.toString())) {
                byte b = decodingMap.get(sb.toString());
                out.write(b);
                sb.setLength(0);
            }
        }
    }

    private void writeBitLength(DataOutputStream out, Map<Byte, Integer> byteToCount, Map<Byte, String> encodingMap) throws IOException {
        long totalBitLength = 0;
        for (Byte b : byteToCount.keySet()) {
            int count = byteToCount.get(b);
            String encoding = encodingMap.get(b);
            assert encoding != null;
            int bitLength = encoding.length();
            totalBitLength = (totalBitLength + count * bitLength);
        }
        out.writeLong(totalBitLength);
    }

    private void writeEncodingMap(Map<Byte, String> encodingMap, DataOutputStream out) throws IOException {
        out.writeInt(encodingMap.size());
        for (Byte b : encodingMap.keySet()) {
            out.writeByte(b);
            writeBitString(encodingMap.get(b), out);
        }
    }

    private Map<String, Byte> readDecodingMap(DataInputStream in) throws IOException {
        Map<String, Byte> decodingMap = new HashMap<>();
        int entryCount = in.readInt();
        for (int i = 0; i < entryCount; i++) {
            byte b = in.readByte();
            String encoding = readBitString(in);
            decodingMap.put(encoding, b);
        }

        return decodingMap;
    }

    private Map<Byte, String> getEncodingMap(Node root) {
        Map<Byte, String> encodingMap = new HashMap<>();
        dfs(root, new StringBuilder(), encodingMap);
        return encodingMap;
    }

    private Map<Byte, Integer> getByteToCount(File source) throws IOException {
        try (InputStream in = new BufferedInputStream(new FileInputStream(source))) {
            Map<Byte, Integer> byteToCount = new HashMap<>();
            int b = in.read();
            while (b != -1) {
                byteToCount.put((byte) b, byteToCount.getOrDefault((byte) b, 0) + 1);
                b = in.read();
            }
            return byteToCount;
        }
    }

    // 构建 Huffman 树，每次选择权值最小的两棵树进行合并
    private Node buildHuffmanTree(Map<Byte, Integer> byteToCount) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (Byte b : byteToCount.keySet()) {
            Node node = new Node();
            node.value = b;
            node.weight = byteToCount.get(b);
            pq.add(node);
        }

        while (pq.size() > 1) {
            Node node1 = pq.poll();
            Node node2 = pq.poll();
            assert node1 != null && node2 != null;
            Node parent = new Node();
            parent.weight = node1.weight + node2.weight;
            parent.left = node1;
            parent.right = node2;
            node1.parent = parent;
            node2.parent = parent;
            pq.add(parent);
        }

        return pq.poll();
    }

    // 通过深度优先搜索确定每个 byte 对应的编码
    private void dfs(Node root, StringBuilder path, Map<Byte, String> encodingMap) {
        // Huffman 树中，一个节点要么没有子节点，要么有两个子节点
        if (root.left == null && root.right == null) { // 叶子节点
            encodingMap.put(root.value, path.toString());
        } else {
            assert root.left != null && root.right != null;
            path.append("0");
            dfs(root.left, path, encodingMap);
            path.append("1");
            dfs(root.right, path, encodingMap);
        }

        if (path.length() > 0) {
            path.deleteCharAt(path.length() - 1);
        }
    }

    private static byte bitsToByte(int[] bits) {
        assert bits.length == 8;
        int value = 0;
        for (int i = 0; i < 8; i++) {
            value += (bits[i] << (7 - i));
        }
        return (byte) value;
    }

    private static void writeBitString(String bitString, DataOutputStream out) throws IOException {
        out.writeInt(bitString.length());
        int[] bits = new int[8];
        int n = 0;
        for (char ch : bitString.toCharArray()) {
            int bit = ch - '0';
            bits[n++] = bit;
            if (n == 8) {
                out.writeByte(bitsToByte(bits));
                n = 0;
            }
        }

        if (n > 0) {
            out.writeByte(bitsToByte(bits));
        }
    }

    private static String readBitString(DataInputStream in) throws IOException {
        int length = in.readInt();
        StringBuilder sb = new StringBuilder();
        int bits = 0;
        for (int i = 0; i < length; i++) {
            if (i % 8 == 0) {
                bits = in.readByte();
            }
            int position = 7 - i % 8;
            int bit = (bits >> position) & 1;
            sb.append(bit);
        }
        return sb.toString();
    }


    private static class Node implements Comparable<Node> {
        byte value;
        int weight;
        Node parent;
        Node left;
        Node right;

        @Override
        public int compareTo(Node o) {
            return Integer.compare(weight, o.weight);
        }
    }

}
