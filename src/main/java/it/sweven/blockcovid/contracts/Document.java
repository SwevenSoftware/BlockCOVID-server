package it.sweven.blockcovid.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.1.
 */
@SuppressWarnings("rawtypes")
public class Document extends Contract {
    public static final String BINARY = "6080604052600180546001600160a01b0319163317905534801561002257600080fd5b5061021d806100326000396000f3fe608060405234801561001057600080fd5b50600436106100365760003560e01c8063b0c8f9dc1461003b578063bb9c6c3e14610064575b600080fd5b61004e6100493660046100e5565b610077565b60405161005b91906101c8565b60405180910390f35b61004e6100723660046100e5565b6100be565b6001546000906001600160a01b0316331461009157600080fd5b6000429050806000846040516100a7919061018f565b908152604051908190036020019020559050919050565b600080826040516100cf919061018f565b9081526020016040518091039020549050919050565b6000602082840312156100f6578081fd5b813567ffffffffffffffff8082111561010d578283fd5b818401915084601f830112610120578283fd5b813581811115610132576101326101d1565b604051601f8201601f19908116603f0116810190838211818310171561015a5761015a6101d1565b81604052828152876020848701011115610172578586fd5b826020860160208301379182016020019490945295945050505050565b60008251815b818110156101af5760208186018101518583015201610195565b818111156101bd5782828501525b509190910192915050565b90815260200190565b634e487b7160e01b600052604160045260246000fdfea2646970667358221220f81004fe0d681a863f47c5016a5b7d5e0a3523cc16b0aa1b190e2cbfe22fb3f364736f6c63430008010033";

    public static final String FUNC_ADD = "add";

    public static final String FUNC_VERIFY = "verify";

    @Deprecated
    protected Document(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Document(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Document(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Document(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> add(String hash) {
        final Function function = new Function(
                FUNC_ADD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(hash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> verify(String hash) {
        final Function function = new Function(FUNC_VERIFY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(hash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @Deprecated
    public static Document load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Document(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Document load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Document(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Document load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Document(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Document load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Document(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Document> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Document.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Document> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Document.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Document> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Document.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Document> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Document.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
