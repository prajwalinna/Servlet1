import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class AdminPage
 */
@WebServlet("/AdminPage")
public class AdminPage extends HttpServlet {
    
    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException,
            IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use
               following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet SessionServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SessionServlet at " +
                    request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException,
            IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        // Check if the logout action is requested
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                logSessionEnd(session); // Log before invalidating
                session.invalidate(); // Invalidate the session
            }
            response.sendRedirect("AdminPage"); // Redirect to the login page
            return;
        }
        
        // Check if session exists
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("githubUsername") != null) {
            // User is logged in, display welcome message and session info
            out.println("<html><head><title>Welcome</title></head><body>");
            out.println("<h2>Welcome, " + session.getAttribute("githubUsername") + "!</h2>");
            out.println("<p><strong>GitHub Email:</strong> " + session.getAttribute("githubEmail") + "</p>");
            out.println("<p><a href='AdminPage?action=logout'>Logout</a></p>");
            out.println("<h3>Session Information:</h3>");
            out.println("<p>Session ID: " + session.getId() + "</p>");
            out.println("<p>Creation Time: " + new java.util.Date(session.getCreationTime()) + "</p>");
            out.println("<p>Last Accessed Time: " + new java.util.Date(session.getLastAccessedTime()) + "</p>");
            out.println("</body></html>");
        } else {
            // User is not logged in, display login form
            out.println("<html><head><title>Login</title></head><body>");
            out.println("<h2>GitHub Session Tracker</h2>");
            out.println("<form method='post' action='AdminPage'>");
            out.println("GitHub Username: <input type='text' name='githubUsername' required><br><br>");
            out.println("GitHub Email: <input type='email' name='githubEmail' required><br><br>");
            out.println("Password: <input type='password' name='password' required><br><br>");
            out.println("<input type='submit' value='Login'>");
            out.println("</form>");
            out.println("<p><small>Password: password</small></p>");
            out.println("</body></html>");
        }
        
        HttpSession sessiona = request.getSession(true);
        
        Integer count = (Integer) sessiona.getAttribute("visitCount");
        if (count == null) {
            count = 1;
        } else {
            count++;
        }
        
        sessiona.setAttribute("visitCount", count);
        
        response.setContentType("text/html");
        PrintWriter outa = response.getWriter();
        
        outa.println("<html><head><title>Visit Counter</title></head><body>");
        outa.println("<h1>Number of visits this session: " + count + "</h1>");
        outa.println("</body></html>");
        
        Path path = Paths.get("C:/Users/prajw/Desktop/Servlet/count.txt");
        String str = Integer.toString(count);
        Files.writeString(path, str, StandardCharsets.UTF_8);
        
        // Update current user file
        updateCurrentUserFile(sessiona, count);
    }
    
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        // Process login form submission
        String githubUsername = request.getParameter("githubUsername");
        String githubEmail = request.getParameter("githubEmail");
        String password = request.getParameter("password");
        
        // For demonstration purposes, validate against hardcoded credentials
        if (githubUsername != null && githubEmail != null &&
                password != null && password.equals("password")) {
            // Login successful, create session and store username
            HttpSession session = request.getSession(true); // create new session if not exists
            session.setAttribute("githubUsername", githubUsername);
            session.setAttribute("githubEmail", githubEmail);
            
            // Log session start
            logSessionStart(session, githubUsername, githubEmail);
            
            // Redirect to doGet to display welcome message and session info
            response.sendRedirect("AdminPage");
        } else {
            // Login failed, redirect back to login page
            response.sendRedirect("AdminPage");
        }
    }
    
    protected void doDelete(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        // Process logout (via DELETE request)
        HttpSession session = request.getSession(false);
        if (session != null) {
            logSessionEnd(session); // Log before invalidating
            session.invalidate(); // invalidate session
        }
        response.sendRedirect("AdminPage");
    }
    
    private void logSessionStart(HttpSession session, String githubUsername, String githubEmail) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String logEntry = String.format("SESSION_START | %s | %s | %s | %s%n", 
                    timestamp, session.getId(), githubUsername, githubEmail);
            
            Path logPath = Paths.get("session-log.txt");
            Files.write(logPath, logEntry.getBytes(StandardCharsets.UTF_8), 
                    java.nio.file.StandardOpenOption.CREATE, 
                    java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void logSessionEnd(HttpSession session) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String githubUsername = (String) session.getAttribute("githubUsername");
            String logEntry = String.format("SESSION_END | %s | %s | %s%n", 
                    timestamp, session.getId(), githubUsername);
            
            Path logPath = Paths.get("session-log.txt");
            Files.write(logPath, logEntry.getBytes(StandardCharsets.UTF_8), 
                    java.nio.file.StandardOpenOption.CREATE, 
                    java.nio.file.StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void updateCurrentUserFile(HttpSession session, int visitCount) {
        try {
            String githubUsername = (String) session.getAttribute("githubUsername");
            String githubEmail = (String) session.getAttribute("githubEmail");
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            String userInfo = String.format("GitHub User: %s%nEmail: %s%nVisits: %d%nLast Access: %s%n",
                    githubUsername, githubEmail, visitCount, timestamp);
            
            Path userPath = Paths.get("current-user.txt");
            Files.write(userPath, userInfo.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
