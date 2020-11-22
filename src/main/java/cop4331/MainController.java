package cop4331;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
//@RequestMapping(path="/demo")
@RequestMapping(path="/controller")
public class MainController {
    @Autowired
    @Qualifier(value = "userRepository")
    private UserRepository userRepository;

    @Autowired
    @Qualifier(value = "transactionRepository")
    private TransactionRepository transactionRepository;

    @Autowired
    @Qualifier(value = "accountRepository")
    private AccountRepository accountRepository;

//    @PostMapping(path="/add") questionable
    @PostMapping(path="/add")
    public @ResponseBody String addNewUser (@RequestParam String uName,
        @RequestParam String fName, @RequestParam String lName, @RequestParam String pWord) {

        User n = new User();
        n.setUName(uName);
        n.setFName(fName);
        n.setLName(lName);
        n.setPWord(pWord);
        List<User> l = userRepository.findByuName(uName);
        if (l.isEmpty()) {
            userRepository.save(n);

            List<User> l2 = userRepository.findByuName(uName);
            User n2 = l2.get(0);    // holds uID

            Account c = new Account();
            c.setUID(n2.getUID());
            c.setAName("Checking");
            c.setIsSavings(false);
            c.setBalance(0.00);
            accountRepository.save(c);
            
            Account s = new Account();
            s.setUID(n2.getUID());
            s.setAName("Savings");
            s.setIsSavings(true);
            s.setBalance(0.00);
            accountRepository.save(s);
            return "Saved\n";
            // also add checking and savings accounts
        }
        return "Duplicate";
    }

    // questionable
    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    // questionable
    @PostMapping(path="/tsxn")
    public @ResponseBody String addTransaction ( @RequestParam Integer sID, @RequestParam Integer rID,
        @RequestParam Double amount, @RequestParam String date, @RequestParam String memo) {

        Transaction n = new Transaction();
        n.setSID(sID);
        n.setRID(rID);
        n.setAmount(amount);
        n.setDate(date);
        n.setMemo(memo);
        transactionRepository.save(n);
        return "Saved\n";
    }

    // questionable
    @GetMapping(path="/test")
    public @ResponseBody String submitSentence ( @RequestParam(value = "sentence", defaultValue = "Hello from Java!") String sentence) {
        TestHTTP n = new TestHTTP(sentence);
        return n.getSentence();
    }

    @GetMapping(path="/history")
    public @ResponseBody Iterable<Transaction> getTransactionHistory() {
        return transactionRepository.findAll();
    }

    @PostMapping(path="/taken")
    public @ResponseBody boolean isuNameTaken ( @RequestParam String uName) {
        List<User> l = userRepository.findByuName(uName);
        if (l.isEmpty()) {
            return false;
        }
        return true;
    }
}
