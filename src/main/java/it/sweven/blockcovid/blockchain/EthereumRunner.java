package it.sweven.blockcovid.blockchain;

import it.sweven.blockcovid.contracts.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

import java.io.IOException;
import java.lang.Exception;

import java.io.FileWriter;

@Component
public class EthereumRunner {
    
    @Autowired private final UserRepository userRepository;

    private Credentials credentials;
    private String contractAddress;
    
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    // abominio trovato online
    public static String bytesToHex(byte[] bytes) {
	char[] hexChars = new char[bytes.length * 2];
	for (int j = 0; j < bytes.length; j++) {
	    int v = bytes[j] & 0xFF;
	    hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	    hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	}
	return new String(hexChars);
    }

    EthereumRunner(UserRepository userRepository) {
	this.userRepository = userRepository;
	Web3j connection = Web3j.build(new HttpService(EthereumConfiguration.NETWORK));
	try {
	    credentials = WalletUtils.loadCredentials(EthereumConfiguration.PASSWORD,
						      EthereumConfiguration.WALLET_FILE);
	} catch (Exception noWalletFile) {
	    credentials = WalletUtils.generateNewWalletFile(EthereumConfiguration.PASSWORD,
							    new File(EthereumConfiguration.WALLET_FILE),
							    true);
	}
	if(connection.getEthCode(EthereumConfig.CONTRACT_ADDRESS).getCode().equals("0x")){
	    // getCode() dovrebbe ritornare il bytecode dello smart
	    // contract, se non c'è, ritorna 0x, il che vuol dire che
	    // il contratto va deployato
	    contractAddress = Document.deploy(connection,
					      credentials,
					      ManagedTransaction.GAS_PRICE,
					      Contract.GAS_LIMIT).send();
	} else {
	    contracAddress = EthereumConfig.CONTRACT_ADDRESS;
	}
    }

    // le cron expression si possono generare online, questa indica di
    // eseguire la funzione ogni giorno all'una di notte
    @Scheduled(cron = "0 0 1 1/1 * ? *")
    public void run() {
	List<User> allUsers = userRepository.findAll();
	FileWriter writer = new FileWriter("allUsers.txt");
	for(User user : allUsers) {
	    writer.write(user.toString() + System.lineSeparator());
	}
	writer.close();

	MessageDigest sha = MessageDigest.getInstance("SHA-256");
	try (InputStream is = Files.newInputStream(Paths.get("allUsers.txt"));
	     DigestInputStream dis = new DigestInputStream(is, sha)){}
	catch (Exception e) { System.err.print("what"); }
	byte[] digest = sha.digest();
	Document.add(bytesToHex(digest));
    }
}
