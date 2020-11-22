package cop4331;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

//    @PostMapping(path="/add") questionable
    @PostMapping(path="/add")
    public @ResponseBody String addNewUser (@RequestParam String uName,
        @RequestParam String fName, @RequestParam String lName, @RequestParam String pWord) {

        User n = new User();
        n.setUName(uName);
        n.setFName(fName);
        n.setLName(lName);
        n.setPWord(pWord);
        userRepository.save(n);
        return "Saved\n";
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
}
