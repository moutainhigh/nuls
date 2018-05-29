package io.nuls.account.rpc.cmd;

import io.nuls.core.tools.cmd.CommandBuilder;
import io.nuls.core.tools.cmd.CommandHelper;
import io.nuls.core.tools.str.StringUtils;
import io.nuls.kernel.lite.annotation.Cmd;
import io.nuls.kernel.lite.annotation.Component;
import io.nuls.kernel.model.CommandResult;
import io.nuls.kernel.model.Result;
import io.nuls.kernel.processor.CommandProcessor;
import io.nuls.kernel.utils.RestFulUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: Charlie
 * @date: 2018/5/25
 */
@Cmd
@Component
public class CreateAccountProcessor implements CommandProcessor {

    private RestFulUtils restFul = RestFulUtils.getInstance();

    @Override
    public String getCommand() {
        return "createaccount";
    }

    @Override
    public String getHelp() {
        CommandBuilder builder = new CommandBuilder();
        builder.newLine(getCommandDescription())
                .newLine("\t[password] The password for the account, the password is between 8 and 20 inclusive of numbers and letters, not encrypted by default");
        return builder.toString();
    }

    @Override
    public String getCommandDescription() {
        return "createaccount [password] --create a account, encrypted by [password] | not encrypted by default";
    }

    @Override
    public boolean argsValidate(String[] args) {
        int length = args.length;
        if (length < 1 || length > 2) {
            return false;
        }
        if (!CommandHelper.checkArgsIsNull(args)) {
            return false;
        }
        if (length == 2 && !StringUtils.validPassword(args[1])) {
            return false;
        }
        return true;
    }

    @Override
    public CommandResult execute(String[] args) {
        String password = args.length == 2 ? args[1] : null;
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("password", password);
        parameters.put("count", 1);
        Result result = restFul.post("/account", parameters);
        if(result.isFailed()){
            return CommandResult.getFailed(result.getMsg());
        }
        return CommandResult.getResult(result);
    }
}
