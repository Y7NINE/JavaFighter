package com.fighting.model;

import java.io.Serializable;

/**
 * 用户类 - 存储用户信息
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;           // 用户ID，格式：heima+5位随机数
    private String username;     // 用户名，唯一，3-16位，字母数字组成，不能纯数字
    private String password;     // 密码，3-8位，字母+数字组合
    private String phone;        // 手机号，纯数字，11位，以1开头
    private boolean locked;      // 账号是否锁定，默认false
    private int loginAttempts;   // 登录失败次数

    /**
     * 构造方法
     * @param id 用户ID
     * @param username 用户名
     * @param password 密码
     */
    public User(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.locked = false;
        this.loginAttempts = 0;
    }

    /**
     * 生成用户ID
     * @return heima+5位随机数
     */
    public static String generateId() {
        int randomNum = (int) (Math.random() * 90000) + 10000; // 10000-99999
        return "heima" + randomNum;
    }

    /**
     * 验证用户名是否合法
     * @param username 用户名
     * @return 是否合法
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.length() < 3 || username.length() > 16) {
            return false;
        }
        // 只能由字母数字组成
        if (!username.matches("^[a-zA-Z0-9]+$")) {
            return false;
        }
        // 不能是纯数字
        if (username.matches("^[0-9]+$")) {
            return false;
        }
        return true;
    }

    /**
     * 验证密码是否合法
     * @param password 密码
     * @return 是否合法
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 3 || password.length() > 8) {
            return false;
        }
        // 只能是字母+数字组合
        if (!password.matches("^[a-zA-Z0-9]+$")) {
            return false;
        }
        // 必须同时包含字母和数字
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        return hasLetter && hasDigit;
    }

    /**
     * 验证手机号是否合法
     * @param phone 手机号
     * @return 是否合法（纯数字，11位，以1开头）
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return false;
        }
        // 纯数字，以1开头
        return phone.matches("^1[0-9]{10}$");
    }

    /**
     * 验证密码是否正确
     * @param inputPassword 输入的密码
     * @return 是否正确
     */
    public boolean checkPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    /**
     * 记录登录失败
     */
    public void recordLoginFailure() {
        this.loginAttempts++;
        if (this.loginAttempts >= 3) {
            this.locked = true;
        }
    }

    /**
     * 重置登录失败次数
     */
    public void resetLoginAttempts() {
        this.loginAttempts = 0;
    }

    /**
     * 解锁账号
     */
    public void unlock() {
        this.locked = false;
        this.loginAttempts = 0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", locked=" + locked +
                ", loginAttempts=" + loginAttempts +
                '}';
    }
}
