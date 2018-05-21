package io.nuls.contract.entity;


import io.nuls.core.tools.crypto.Hex;

import java.io.IOException;
import java.io.Serializable;

/**
 * @Desription:
 * @Author: PierreLuo
 * @Date: 2018/5/2
 */
public class BlockHeader implements Serializable {

    private String hash;
    private String preHash;
    private String merkleHash;
    private long time;
    private long height;
    private long txCount;
    private byte[] packingAddress;//23 bytes
    private String signature ;

    public BlockHeader() {}

    public BlockHeader(io.nuls.kernel.model.BlockHeader header) throws IOException {
        this.hash = header.getHash().getDigestHex();
        this.preHash = header.getPreHash().getDigestHex();
        this.merkleHash = header.getMerkleHash().getDigestHex();
        this.time = header.getTime();
        this.height = header.getHeight();
        this.txCount = header.getTxCount();
        this.packingAddress = header.getPackingAddress();
        this.signature = Hex.encode(header.getScriptSig().serialize());
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreHash() {
        return preHash;
    }

    public void setPreHash(String preHash) {
        this.preHash = preHash;
    }

    public String getMerkleHash() {
        return merkleHash;
    }

    public void setMerkleHash(String merkleHash) {
        this.merkleHash = merkleHash;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public long getTxCount() {
        return txCount;
    }

    public void setTxCount(long txCount) {
        this.txCount = txCount;
    }

    public byte[] getPackingAddress() {
        return packingAddress;
    }

    public void setPackingAddress(byte[] packingAddress) {
        this.packingAddress = packingAddress;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
