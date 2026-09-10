package com.fighting.system;

import com.fighting.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 用户管理类 - 处理注册、登录和用户数据存储
 */
public class UserManager {
    private static final String DATA_DIR = "data";
    private static final String USER_FILE = "data/users.dat";

    private List<User> users;
    private User currentUser;
    private String currentCaptcha;
    private int loginAttempts;

    public UserManager() {
        this.users = new ArrayList<>();
        this.currentUser = null;
        this.currentCaptcha = "";
        this.loginAttempts = 0;
        loadUsers();
    }

    /**
     * 加载用户数据
     */
    @SuppressWarnings("unchecked")
    private void loadUsers() {
        File file = new File(USER_FILE);
        if (!file.exists()) {
            // 确保data目录存在
            File dataDir = new File(DATA_DIR);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            users = (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("加载用户数据失败: " + e.getMessage());
            users = new ArrayList<>();
        }
    }

    /**
     * 保存用户数据
     */
    private void saveUsers() {
        try {
            File dataDir = new File(DATA_DIR);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
                oos.writeObject(users);
            }
        } catch (IOException e) {
            System.out.println("保存用户数据失败: " + e.getMessage());
        }
    }

    /**
     * 注册用户
     * @param username 用户名
     * @param password 密码
     * @param confirmPassword 确认密码
     * @return 注册结果消息，null表示成功
     */
    public String register(String username, String password, String confirmPassword) {
        return register(username, password, confirmPassword, null);
    }

    /**
     * 注册用户（带手机号）
     * @param username 用户名
     * @param password 密码
     * @param confirmPassword 确认密码
     * @param phone 手机号
     * @return 注册结果消息，null表示成功
     */
    public String register(String username, String password, String confirmPassword, String phone) {
        // 验证用户名
        if (!User.isValidUsername(username)) {
            return "用户名不合法！长度3-16位，只能由字母数字组成，不能是纯数字";
        }

        // 检查用户名是否已存在
        if (isUsernameExists(username)) {
            return "用户名已存在！";
        }

        // 验证密码
        if (!User.isValidPassword(password)) {
            return "密码不合法！长度3-8位，只能是字母+数字组合";
        }

        // 验证两次密码是否一致
        if (!password.equals(confirmPassword)) {
            return "两次输入的密码不一致！";
        }

        // 验证手机号（如果提供了）
        if (phone != null && !phone.isEmpty()) {
            if (!User.isValidPhone(phone)) {
                return "手机号不合法！纯数字，11位，以1开头";
            }
        }

        // 创建用户
        String id = User.generateId();
        User newUser = new User(id, username, password);
        if (phone != null && !phone.isEmpty()) {
            newUser.setPhone(phone);
        }
        users.add(newUser);
        saveUsers();

        return null; // 注册成功
    }

    /**
     * 检查用户名是否存在
     */
    private boolean isUsernameExists(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生成验证码
     * @return 长度为5的验证码，由4位大写或小写字母和1位数字组成
     */
    public String generateCaptcha() {
        Random random = new Random();
        StringBuilder captcha = new StringBuilder();
        String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        // 生成4位字母
        for (int i = 0; i < 4; i++) {
            captcha.append(letters.charAt(random.nextInt(letters.length())));
        }

        // 生成1位数字
        captcha.append(random.nextInt(10));

        // 随机打乱顺序
        String captchaStr = captcha.toString();
        char[] chars = captchaStr.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

        currentCaptcha = new String(chars);
        return currentCaptcha;
    }

    /**
     * 验证验证码
     * @param inputCaptcha 用户输入的验证码
     * @return 是否正确
     */
    public boolean verifyCaptcha(String inputCaptcha) {
        return currentCaptcha.equalsIgnoreCase(inputCaptcha);
    }

    /**
     * 登录
     * @param username 用户名
     * @param password 密码
     * @param inputCaptcha 用户输入的验证码
     * @return 登录结果消息，null表示成功
     */
    public String login(String username, String password, String inputCaptcha) {
        // 验证验证码
        if (!verifyCaptcha(inputCaptcha)) {
            return "验证码错误！";
        }

        // 查找用户
        User user = findUser(username);
        if (user == null) {
            return "用户名不存在！";
        }

        // 检查账号是否锁定
        if (user.isLocked()) {
            return "账号已锁定！密码连续输错3次，请联系管理员解锁";
        }

        // 验证密码
        if (!user.checkPassword(password)) {
            user.recordLoginFailure();
            saveUsers();
            if (user.isLocked()) {
                return "密码错误！账号已被锁定";
            }
            return "密码错误！还剩" + (3 - user.getLoginAttempts()) + "次机会";
        }

        // 登录成功
        user.resetLoginAttempts();
        saveUsers();
        currentUser = user;
        return null; // 登录成功
    }

    /**
     * 查找用户
     */
    public User findUser(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * 忘记密码 - 通过手机号重置密码
     * @param username 用户名
     * @param phone 手机号
     * @param newPassword 新密码
     * @return 结果消息，null表示成功
     */
    public String forgotPassword(String username, String phone, String newPassword) {
        // 查找用户
        User user = findUser(username);
        if (user == null) {
            return "当前用户名未注册！";
        }

        // 验证手机号
        if (user.getPhone() == null || !user.getPhone().equals(phone)) {
            return "手机号验证失败！";
        }

        // 验证新密码
        if (!User.isValidPassword(newPassword)) {
            return "新密码不合法！长度3-8位，只能是字母+数字组合";
        }

        // 修改密码
        user.setPassword(newPassword);
        // 解锁账号（如果被锁定）
        if (user.isLocked()) {
            user.unlock();
        }
        saveUsers();

        return null; // 修改成功
    }

    /**
     * 解锁账号（管理员功能）
     * @param username 用户名
     * @return 是否成功
     */
    public boolean unlockAccount(String username) {
        User user = findUser(username);
        if (user != null) {
            user.unlock();
            saveUsers();
            return true;
        }
        return false;
    }

    /**
     * 注销
     */
    public void logout() {
        currentUser = null;
        loginAttempts = 0;
    }

    // Getters
    public User getCurrentUser() {
        return currentUser;
    }

    public String getCurrentCaptcha() {
        return currentCaptcha;
    }

    public int getLoginAttempts() {
        return loginAttempts;
    }

    public List<User> getUsers() {
        return users;
    }
}
