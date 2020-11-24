package cop4331;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

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
            s.setBalance(100.00);
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
    @GetMapping(path="/allacct")
    public @ResponseBody Iterable<Account> getAllAccounts() {
        return accountRepository.findAll();
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

    // remove
    @PostMapping(path="/boof")
    public @ResponseBody String showForm ( @RequestParam String uname, @RequestParam String fname, @RequestParam String lname,
        @RequestParam String password, @RequestParam String confirm) {
        
        return ("" + uname + " " + fname + " " + lname + " " + password + " " + confirm);
    }

    // replaced by login2
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

    @PostMapping(path="/login2")
    public ResponseEntity<String> loginTwo(@RequestParam String uname, @RequestParam String password, HttpServletResponse response) throws NoSuchAlgorithmException {

        String hashedPassword = BankSecurity.hash(password);
        HttpHeaders headers = new HttpHeaders();

        List<User> l = userRepository.findByuName(uname);
        if (l.isEmpty()) {
            // user not found
            headers.add("Location", "http://localhost:8080/login.htm");
            return new ResponseEntity<>("User Not Found", headers, HttpStatus.UNAUTHORIZED);

        }
        // check if passwords match
        User n = l.get(0);
        if (n.getPWord().equals(hashedPassword)) {
            // send to overview with cookie
            headers.add("Location", "http://localhost:8080/secure/overview.htm");

            Cookie cookie = new Cookie("sessionID", uname);
            cookie.setSecure(true);
            //cookie.setPath("/secure/");
            response.addCookie(cookie);
            
            return new ResponseEntity<String>(null, headers, HttpStatus.FOUND);

        } else {
            // wrong password
            headers.add("Location", "http://localhost:8080/login.htm");
            return new ResponseEntity<>("Invalid Password", headers, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping(path="/payment")
    public ResponseEntity<String> performPayment(@RequestParam String account, @RequestParam String runame,
        @RequestParam String amount, @RequestParam String memo, @CookieValue(value="sessionID", defaultValue="INVALID") String sessionID) {
        
        Double dAmount = Double.parseDouble(amount);
        HttpHeaders headers = new HttpHeaders();
        
        // check if cookie is valid
        if (sessionID.equals("INVALID")) {
            // say session is not valid and return
            headers.add("Location", "http://localhost:8080/login.htm");
            return new ResponseEntity<>("Invalid Session", headers, HttpStatus.UNAUTHORIZED);
        }
        List<User> l = userRepository.findByuName(sessionID); // subject to change
        User u;
        if (l.isEmpty()) {
            // idk how you got here, seems kinda sus
            headers.add("Location", "http://localhost:8080/login.htm");
            return new ResponseEntity<>(null, headers, HttpStatus.UNAUTHORIZED);
        } else {
            u = l.get(0);
        }

        // check if money is available in selected account
        // first find the account
        List<Account> la = accountRepository.findByuID(u.getUID());
        Account acc = BankSecurity.findAccount(la, account);
        // then see if there's enough money
        if (acc.getBalance() < dAmount) {
            // not enough money
            headers.add("Location", "http://localhost:8080/login.htm");
            return new ResponseEntity<>("Not Enough Money", headers, HttpStatus.UNAUTHORIZED);
        }
        
        // check if recipient is valid
        List<User> lr = userRepository.findByuName(runame); // subject to change
        if (lr.isEmpty()) {
            // user not found
            headers.add("Location", "http://localhost:8080/login.htm");
            return new ResponseEntity<>("User Not Found", headers, HttpStatus.UNAUTHORIZED);
        }
        User r = lr.get(0);
        
        // make transaction
        Transaction t = new Transaction();
        t.setSID(u.getUID());
        t.setRID(r.getUID());
        t.setAmount(dAmount);
        t.setDate(new Date().toString());
        t.setMemo(memo);
        transactionRepository.save(t);
        
        la = accountRepository.findByuID(r.getUID());
        Account racc = BankSecurity.findAccount(la, "Checking");
        // edit user's accounts
        acc.setBalance(acc.getBalance() - dAmount);
        accountRepository.save(acc);
        racc.setBalance(racc.getBalance() + dAmount);
        accountRepository.save(racc);

        // return to overview
        headers.add("Location", "http://localhost:8080/secure/overview.htm");
        return new ResponseEntity<String>(null, headers, HttpStatus.FOUND);
    }
}
