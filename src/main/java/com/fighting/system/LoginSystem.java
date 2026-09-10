package com.fighting.system;

import com.fighting.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * 登录注册系统 - 文字版登录、注册、忘记密码
 */
public class LoginSystem {

    private final List<User> userList = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);
    private User currentUser;

    public LoginSystem() {
        // 添加测试用户
        User testUser = new User(User.generateId(), "test1", "abc123");
        testUser.setPhone("13800138000");
        userList.add(testUser);
    }

    /**
     * 显示主菜单并处理用户操作
     * @return 登录成功返回用户名，退出返回null
     */
    public String showMainMenu() {
        while (true) {
            System.out.println("╔════════════════════════════════╗");
            System.out.println("    欢迎来到文字格斗游戏   ");
            System.out.println("╚════════════════════════════════╝");
            System.out.println("请选择操作：1登录 2注册 3忘记密码 4退出");
            System.out.print("请输入选择: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    String username = login();
                    if (username != null) {
                        return username;
                    }
                    break;
                case "2":
                    register();
                    break;
                case "3":
                    forgotPassword();
                    break;
                case "4":
                    System.out.println("感谢游玩，再见！");
                    return null;
                default:
                    System.out.println("无效选择，请重新输入！");
            }
            System.out.println();
        }
    }

    /**
     * 登录功能
     * @return 登录成功返回用户名，失败返回null
     */
    private String login() {
        System.out.println("\n===== 登录 =====");

        // 输入用户名
        System.out.print("请输入用户名: ");
        String username = scanner.nextLine().trim();

        // 查找用户
        User user = findUser(username);
        if (user == null) {
            System.out.println("用户名未注册，请先注册！");
            return null;
        }

        // 检查账号是否锁定
        if (user.isLocked()) {
            System.out.println("用户 " + username + " 已经锁定，请联系客服：400-1234567");
            return null;
        }

        // 输入密码（最多3次机会）
        for (int i = 0; i < 3; i++) {
            System.out.print("请输入密码: ");
            String password = scanner.nextLine().trim();

            // 输入验证码
            String captcha = generateCaptcha();
            System.out.println("验证码: " + captcha);
            System.out.print("请输入验证码: ");
            String inputCaptcha = scanner.nextLine().trim();

            if (!inputCaptcha.equals(captcha)) {
                System.out.println("验证码输入错误，请重新输入！");
                continue;
            }

            if (user.checkPassword(password)) {
                // 登录成功
                user.resetLoginAttempts();
                System.out.println("登录成功！欢迎回来，" + username + "！");
                this.currentUser = user;
                return username;
            } else {
                user.recordLoginFailure();
                int remaining = 3 - (i + 1);
                if (remaining > 0) {
                    System.out.println("密码错误，还剩 " + remaining + " 次机会！");
                } else {
                    System.out.println("密码连续输错3次，账号已锁定！");
                    System.out.println("请联系客服：400-1234567");
                }
            }
        }

        return null;
    }

    /**
     * 注册功能
     */
    private void register() {
        System.out.println("\n===== 注册 =====");

        // 输入用户名
        System.out.print("请输入用户名(3-16位，字母数字组成，不能纯数字): ");
        String username = scanner.nextLine().trim();

        if (!User.isValidUsername(username)) {
            System.out.println("用户名格式不正确！");
            return;
        }

        // 检查用户名是否已存在
        if (findUser(username) != null) {
            System.out.println("用户名已存在，请重新选择！");
            return;
        }

        // 输入密码
        System.out.print("请输入密码(3-8位，字母+数字组合): ");
        String password = scanner.nextLine().trim();

        if (!User.isValidPassword(password)) {
            System.out.println("密码格式不正确！");
            return;
        }

        // 确认密码
        System.out.print("请再次输入密码: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!password.equals(confirmPassword)) {
            System.out.println("两次输入的密码不一致！");
            return;
        }

        // 输入手机号
        System.out.print("请输入手机号(11位，以1开头): ");
        String phone = scanner.nextLine().trim();

        if (!User.isValidPhone(phone)) {
            System.out.println("手机号格式不正确！");
            return;
        }

        // 创建用户
        String id = User.generateId();
        User newUser = new User(id, username, password);
        newUser.setPhone(phone);
        userList.add(newUser);

        System.out.println("注册成功！");
        System.out.println("用户ID: " + id);
        System.out.println("用户名: " + username);
    }

    /**
     * 忘记密码功能
     */
    private void forgotPassword() {
        System.out.println("\n===== 忘记密码 =====");

        // 输入用户名
        System.out.print("请输入用户名: ");
        String username = scanner.nextLine().trim();

        // 查找用户
        User user = findUser(username);
        if (user == null) {
            System.out.println("当前用户名未注册！");
            return;
        }

        // 输入手机号验证
        System.out.print("请输入注册时的手机号: ");
        String phone = scanner.nextLine().trim();

        if (!phone.equals(user.getPhone())) {
            System.out.println("手机号验证失败！");
            return;
        }

        // 手机号正确，输入新密码
        System.out.print("请输入新密码(3-8位，字母+数字组合): ");
        String newPassword = scanner.nextLine().trim();

        if (!User.isValidPassword(newPassword)) {
            System.out.println("密码格式不正确！");
            return;
        }

        // 确认新密码
        System.out.print("请再次输入新密码: ");
        String confirmPassword = scanner.nextLine().trim();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("两次输入的密码不一致！");
            return;
        }

        // 修改密码
        user.setPassword(newPassword);
        // 解锁账号（如果被锁定）
        if (user.isLocked()) {
            user.unlock();
        }
        System.out.println("密码修改成功！");
    }

    /**
     * 查找用户
     */
    private User findUser(String username) {
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * 生成验证码：5位，4位字母+1位数字
     */
    private String generateCaptcha() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        // 生成4个随机字母
        for (int i = 0; i < 4; i++) {
            sb.append(letters.charAt(random.nextInt(letters.length())));
        }

        // 生成1个随机数字
        sb.append(random.nextInt(10));

        // 打乱顺序
        char[] chars = sb.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

        return new String(chars);
    }

    /**
     * 获取当前登录用户
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * 获取用户列表
     */
    public List<User> getUserList() {
        return userList;
    }
}
