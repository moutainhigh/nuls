package contracts.crowdsale;

import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Event;
import io.nuls.contract.sdk.Msg;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigInteger;

import static io.nuls.contract.sdk.Utils.emit;
import static io.nuls.contract.sdk.Utils.require;

public class Crowdsale {

    private Address token;

    private Address wallet;

    private BigInteger rate;

    private BigInteger weiRaised = BigInteger.ZERO;

    public Address getToken() {
        return token;
    }

    public Address getWallet() {
        return wallet;
    }

    public BigInteger getRate() {
        return rate;
    }

    public BigInteger getWeiRaised() {
        return weiRaised;
    }

    @Data
    @AllArgsConstructor
    class TokenPurchaseEvent implements Event {

        private Address purchaser;

        private Address beneficiary;

        private BigInteger value;

        private BigInteger amount;

    }

    public Crowdsale(BigInteger rate, Address wallet, Address token) {
        require(rate != null && rate.compareTo(BigInteger.ZERO) > 0);
        require(wallet != null);
        require(token != null);

        this.rate = rate;
        this.wallet = wallet;
        this.token = token;
    }

//    function () external payable {
//        buyTokens(msg.sender);
//    }

    public void buyTokens(Address beneficiary) {

        BigInteger weiAmount = Msg.value();
        preValidatePurchase(beneficiary, weiAmount);

        BigInteger tokens = getTokenAmount(weiAmount);

        weiRaised = weiRaised.add(weiAmount);

        processPurchase(beneficiary, tokens);
        emit(new TokenPurchaseEvent(Msg.sender(), beneficiary, weiAmount, tokens));

        updatePurchasingState(beneficiary, weiAmount);

        forwardFunds();
        postValidatePurchase(beneficiary, weiAmount);
    }

    protected void preValidatePurchase(Address beneficiary, BigInteger weiAmount) {
        require(beneficiary != null);
        require(weiAmount != null && weiAmount.compareTo(BigInteger.ZERO) > 0);
    }

    protected void postValidatePurchase(Address beneficiary, BigInteger weiAmount) {
        // optional override
    }

    protected void deliverTokens(Address beneficiary, BigInteger tokenAmount) {
        String[] args = new String[]{beneficiary.toString(), tokenAmount.toString()};
        token.call("transfer", args);
    }

    protected void processPurchase(Address beneficiary, BigInteger tokenAmount) {
        deliverTokens(beneficiary, tokenAmount);
    }

    protected void updatePurchasingState(Address beneficiary, BigInteger weiAmount) {
        // optional override
    }

    protected BigInteger getTokenAmount(BigInteger weiAmount) {
        return weiAmount.multiply(rate);
    }

    protected void forwardFunds() {
        wallet.transfer(Msg.value());
    }

}
