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

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
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

    @PostMapping(path="/add")
    public @ResponseBody String addNewUser (@RequestParam String uname,
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
            c.setUName(n2.getUName());
            c.setAName("Checking");
            c.setIsSavings(false);
            c.setBalance(100.00);   // temp, should be 0
            accountRepository.save(c);
            
            Account s = new Account();
            s.setUName(n2.getUName());
            s.setAName("Savings");
            s.setIsSavings(true);
            s.setBalance(100.00);   // temp, should be 0
            accountRepository.save(s);
            System.out.println("login success yay yippee!");
            return "success";
            // also add checking and savings accounts
        }
        return "INVALID";
    }
    
    @GetMapping(path="/history")
    public @ResponseBody Iterable<Transaction> getTransactionHistory(@CookieValue(value="sessionID", defaultValue="INVALID") String sessionID, @RequestParam String account) {
        
        if (sessionID.equals("INVALID")) {
            // invalid ID
            return null;
        }
        Boolean isSavings = false;
        if (account.equals("Savings")) {
            isSavings = true;
        }
        // convert sessionID to uID (subject to change)
        List<User> ul = userRepository.findByuName(sessionID); // subject to change
        User u = ul.get(0);
        
        List<Transaction> ret = new ArrayList<>();
        // find all where senderID matches
        List<Transaction> l1 = transactionRepository.findBysUName(u.getUName());
        for (Transaction t : l1) {
            if (t.getsAcctSavings() == isSavings) {
                ret.add(t);
            }
        }
        // find all where recieverID matches
        List<Transaction> l2 = transactionRepository.findByrUName(u.getUName());
        for (Transaction t : l2) {
            if (t.getrAcctSavings() == isSavings) {
                ret.add(t);
            }
        }

        return ret;
    }
    
    @PostMapping(path="/login")
    public ResponseEntity<String> loginTwo(@RequestParam String uname, @RequestParam String password, HttpServletResponse response) throws NoSuchAlgorithmException {
        
        String hashedPassword = BankSecurity.hash(password);
        HttpHeaders headers = new HttpHeaders();
        
        List<User> l = userRepository.findByuName(uname);
        if (l.isEmpty()) {
            // user not found
            return new ResponseEntity<>("<h2>Error: User Not Found</h2><p><a href='http://localhost:8080/login.htm'>Click here to go back to login.</a></p>", headers, HttpStatus.UNAUTHORIZED);
            
        }
        // check if passwords match
        User n = l.get(0);
        if (n.getPWord().equals(hashedPassword)) {
            // send to overview with cookie
            headers.add("Location", "http://localhost:8080/secure/overview.htm");
            
            Cookie cookie = new Cookie("sessionID", uname);
            cookie.setSecure(true);
            response.addCookie(cookie);
            
            return new ResponseEntity<String>(null, headers, HttpStatus.FOUND);
            
        } else {
            // wrong password
            return new ResponseEntity<>("<h2>Error: Invalid Password</h2><p><a href='http://localhost:8080/login.htm'>Click here to go back to login.</a></p>", headers, HttpStatus.UNAUTHORIZED);
        }
    }
    
    @PostMapping(path="/payment")
    public ResponseEntity<String> performPayment(@RequestParam String account, @RequestParam String runame,
    @RequestParam String amount, @RequestParam String raccount, @RequestParam String memo, @CookieValue(value="sessionID", defaultValue="INVALID") String sessionID) {
        
        HttpHeaders headers = new HttpHeaders();
        Double dAmount;
        try {
            dAmount = Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>("<h2>Error: Amount Must Be A Valid Number</h2><p><a href='http://localhost:8080/secure/payment.htm'>Click here to go back to payment.</a></p>", headers, HttpStatus.UNAUTHORIZED);   
        }
        if (dAmount <= 0) {
            return new ResponseEntity<>("<h2>Error: Amount Must Be A Valid Number</h2><p><a href='http://localhost:8080/secure/payment.htm'>Click here to go back to payment.</a></p>", headers, HttpStatus.UNAUTHORIZED);   
        }
        // round to two places
        dAmount = Math.round(dAmount * 100.0) / 100.0;

        
        // check if cookie is valid
        if (sessionID.equals("INVALID")) {
            // say session is not valid and return
            return new ResponseEntity<>("<h2>Invalid Session</h2><p><a href='http://localhost:8080/login.htm'>Click here to go back to login.</a></p>", headers, HttpStatus.UNAUTHORIZED);
        }
        List<User> l = userRepository.findByuName(sessionID); // subject to change
        User u;
        if (l.isEmpty()) {
            // idk how you got here, seems kinda sus
            return new ResponseEntity<>("<h2>Invalid Session</h2><p><a href='http://localhost:8080/login.htm'>Click here to go back to login.</a></p>", headers, HttpStatus.UNAUTHORIZED);
        } else {
            u = l.get(0);
        }
        
        // check if money is available in selected account
        List<Account> la = accountRepository.findByuName(u.getUName());
        Account acc = BankSecurity.findAccount(la, account);
        if (acc.getBalance() < dAmount) {
            // not enough money
            return new ResponseEntity<>("<h2>Not Enough Money</h2><p><a href='http://localhost:8080/secure/payment.htm'>Click here to go back to payment.</a></p>", headers, HttpStatus.UNAUTHORIZED);
        }
        
        // check if recipient is valid
        List<User> lr = userRepository.findByuName(runame); // subject to change
        if (lr.isEmpty()) {
            // user not found
            return new ResponseEntity<>("<h2>User Not Found</h2><p><a href='http://localhost:8080/secure/payment.htm'>Click here to go back to payment.</a></p>", headers, HttpStatus.UNAUTHORIZED);
        }
        User r = lr.get(0);
        la = accountRepository.findByuName(r.getUName());
        Account racc = BankSecurity.findAccount(la, raccount);
        
        // make transaction
        Transaction t = new Transaction();
        t.setSUName(u.getUName());
        t.setsAcctSavings(acc.getIsSavings());
        t.setRUName(r.getUName());
        t.setrAcctSavings(racc.getIsSavings());
        t.setAmount(dAmount);
        t.setDate(new Date().toString());
        t.setMemo(memo);
        transactionRepository.save(t);
        
        // edit user's accounts
        acc.setBalance(acc.getBalance() - dAmount);
        accountRepository.save(acc);
        racc.setBalance(racc.getBalance() + dAmount);
        accountRepository.save(racc);
        
        // return to overview
        headers.add("Location", "http://localhost:8080/secure/overview.htm");
        return new ResponseEntity<String>(null, headers, HttpStatus.FOUND);
    }

    @GetMapping("/overview")
    public @ResponseBody Iterable<Account> getAccountBalances(@CookieValue(value="sessionID", defaultValue="INVALID") String sessionID) {
        
        if (sessionID.equals("INVALID")) {
            // invalid ID
            return null;
        }   
        // convert sessionID to uID (subject to change)
        List<User> ul = userRepository.findByuName(sessionID); // subject to change
        User u = ul.get(0);
        
        return accountRepository.findByuName(u.getUName());
    }

    @GetMapping(path="/cookie")
    public ResponseEntity<String> isValidCookie(@CookieValue(value="sessionID", defaultValue="INVALID") String sessionID) {
        if (sessionID.equals("INVALID")) {
            return new ResponseEntity<>("<h2>Invalid Session</h2><p><a href='http://localhost:8080/login.htm'>Click here to go back to login.</a></p>", null, HttpStatus.UNAUTHORIZED);
        }
        List<User> l = userRepository.findByuName(sessionID); // subject to change again
        if (l.isEmpty()) {
            return new ResponseEntity<>("<h2>Invalid Session</h2><p><a href='http://localhost:8080/login.htm'>Click here to go back to login.</a></p>", null, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(null, null, HttpStatus.OK);
    }
    
    @GetMapping(path="/remove")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "http://localhost:8080/login.htm");

        Cookie cookie = new Cookie("sessionID", null);
        cookie.setMaxAge(0);
        cookie.setSecure(true);
        response.addCookie(cookie);
        
        return new ResponseEntity<String>(null, headers, HttpStatus.FOUND);
    }

    // questionable/bugtest
    @GetMapping(path="/all")
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
    @GetMapping(path="/allacct")
    public @ResponseBody Iterable<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    @GetMapping(path="/test")
    public @ResponseBody String submitSentence ( @RequestParam(value = "sentence", defaultValue = "Hello from Java!") String sentence) {
        TestHTTP n = new TestHTTP(sentence);
        return n.getSentence();
    }
}
