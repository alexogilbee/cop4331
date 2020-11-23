package cop4331;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.security.NoSuchAlgorithmException;
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
    public @ResponseBody ModelAndView addNewUser (@RequestParam String uname,
        @RequestParam String fname, @RequestParam String lname, @RequestParam String password) throws NoSuchAlgorithmException {

        String hashedPassword = BankSecurity.hash(password);

        User n = new User();
        n.setUName(uname);
        n.setFName(fname);
        n.setLName(lname);
        n.setPWord(hashedPassword);
        List<User> l = userRepository.findByuName(uname);
        if (l.isEmpty()) {
            userRepository.save(n);

            List<User> l2 = userRepository.findByuName(uname);
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
            return new ModelAndView("redirect:http://localhost:8080/login.htm");
            // also add checking and savings accounts
        }
        return new ModelAndView("redirect:http://localhost:8080/signup.htm");
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

    @PostMapping(path="/boof")
    public @ResponseBody String showForm ( @RequestParam String uname, @RequestParam String fname, @RequestParam String lname,
        @RequestParam String password, @RequestParam String confirm) {
        
        return ("" + uname + " " + fname + " " + lname + " " + password + " " + confirm);
    }

    @PostMapping(path="/login")
    public @ResponseBody ModelAndView verifyLogin(@RequestParam String uname, @RequestParam String password) throws NoSuchAlgorithmException {
        
        String hashedPassword = BankSecurity.hash(password);

        List<User> l = userRepository.findByuName(uname);
        if (l.isEmpty()) {
            // user not found
            return new ModelAndView("redirect:http://localhost:8080/login.htm");
        }
        // check if passwords match
        User n = l.get(0);
        if (n.getPWord().equals(hashedPassword)) {
            // send to overview
            // (also make cookies but idk how to yet)
            return new ModelAndView("redirect:http://localhost:8080/secure/overview.htm");
        } else {
            // wrong password
            return new ModelAndView("redirect:http://localhost:8080/login.htm");
        }
    }
}
