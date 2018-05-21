package contracts.token.ERC20;

import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Msg;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static io.nuls.contract.sdk.Utils.emit;

public class StandardToken extends BasicToken implements ERC20 {

    protected Map<Address, Map<Address, BigInteger>> allowed = new HashMap<>();

    @Override
    public BigInteger allowance(Address owner, Address spender) {
        return allowed.getOrDefault(owner, new HashMap<>()).getOrDefault(spender, BigInteger.ZERO);
    }

    @Override
    public boolean transferFrom(Address from, Address to, BigInteger value) {
        subtractBalance(from, value);
        addBalance(to, value);
        subtractAllowed(from, Msg.sender(), value);
        emit(new TransferEvent(from, to, value));
        return true;
    }

    @Override
    public boolean approve(Address spender, BigInteger value) {
        setAllowed(Msg.sender(), spender, value);
        emit(new ApprovalEvent(Msg.sender(), spender, value));
        return true;
    }

    public boolean increaseApproval(Address spender, BigInteger addedValue) {
        addAllowed(Msg.sender(), spender, addedValue);
        emit(new ApprovalEvent(Msg.sender(), spender, allowance(Msg.sender(), spender)));
        return true;
    }

    public boolean decreaseApproval(Address spender, BigInteger subtractedValue) {
        check(subtractedValue);
        BigInteger oldValue = allowance(Msg.sender(), spender);
        if (subtractedValue.compareTo(oldValue) > 0) {
            setAllowed(Msg.sender(), spender, BigInteger.ZERO);
        } else {
            subtractAllowed(Msg.sender(), spender, subtractedValue);
        }
        emit(new ApprovalEvent(Msg.sender(), spender, allowance(Msg.sender(), spender)));
        return true;
    }

    protected void addAllowed(Address address1, Address address2, BigInteger value) {
        BigInteger allowance = allowance(address1, address2);
        check(allowance);
        check(value);
        setAllowed(address1, address2, allowance.add(value));
    }

    protected void subtractAllowed(Address address1, Address address2, BigInteger value) {
        BigInteger allowance = allowance(address1, address2);
        check(allowance, value);
        setAllowed(address1, address2, allowance.subtract(value));
    }

    protected void setAllowed(Address address1, Address address2, BigInteger value) {
        check(value);
        allowed.putIfAbsent(address1, new HashMap<>()).put(address2, value);
    }

}
