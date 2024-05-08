package lhx;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Stack;
import javax.sound.sampled.*;

public class MainWindow extends JFrame {
    private JTextArea result;//显示结果的文本框
    private JTextArea history_result;//显示历史记录的文本框
    private final int btn_nums = 22;//按钮个数
    private final JButton[] btn_sets = new JButton[btn_nums];//按钮数组
    private boolean done = true;//是否完成计算
    //计算表达式的值
    Stack<Character> operatorStack = new Stack<>();//运算符栈

    Stack<Double> numberStack = new Stack<>();//数字栈
    private long factorial(int n) { // 计算阶乘
        if (n < 0) {
            throw new IllegalArgumentException("阶乘的值必须是非负整数");
        }
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
    //改
    public void playSound(String soundFileName) {//播放音效
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundFileName).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

//    private double squareRoot(double num) {
//        if (num < 0) {
//            throw new IllegalArgumentException("不能对负数取平方根");
//        }
//        return Math.sqrt(num);
//    }
    public MainWindow(){

        super("计算器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭窗口时退出程序
        Image img = Toolkit.getDefaultToolkit().getImage("title.png");//窗口图标
        setIconImage(img);//设置图标
        //设置系统主题

        String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
            e1.printStackTrace();
        }
        //设置窗口背景色
        getContentPane().setBackground(Color.red);

        //设置布局
        setLayout(new GridLayout(1,2,2,0));
        JPanel workSpace = new JPanel();    //工作区
        //workSpace.setBackground(new Color(128, 0, 128));
        add(workSpace);
        JPanel historySpace = new JPanel();
        historySpace.setBackground(Color.gray);//设置背景色
        add(historySpace);

        //上边显示结果的区域
        workSpace.setLayout(new BorderLayout());//设置布局
        result = new JTextArea(8,10);//显示结果的文本框
        result.setBackground(new Color(150,190,129));//设置背景色
        result.setFont(new Font("微软雅黑",Font.BOLD,25));
        result.setEditable(false);//不可编辑
        result.setLineWrap(true);//自动换行
        result.setFocusable(true);//设置焦点
        result.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
        workSpace.add(result, BorderLayout.NORTH);

        //下边按钮区域
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new GridLayout(6,4));
        for(int i = 0; i < btn_nums; i++) {
            String[] btn_text = {
                    "(", ")", "C", "÷",
                    "7", "8", "9", "x",
                    "4", "5", "6", "-",
                    "1", "2", "3", "+",
                    "±", "0", ".", "!",
                    "√", "="
            };
            btn_sets[i] = new JButton(btn_text[i]);
            btn_sets[i].addActionListener(new ButtonMonitor());
            btn_sets[i].setFont(new Font("微软雅黑",Font.PLAIN,20));
            btn_sets[i].setFocusable(false);

            btnPanel.add(btn_sets[i]);
        }
        workSpace.add(btnPanel);

        //右边历史区域
        historySpace.setLayout(new BorderLayout());

        JLabel history_title = new JLabel("历史记录");
        history_title.setFont(new Font("微软雅黑",Font.BOLD,20));
        historySpace.add(history_title, BorderLayout.NORTH);

        JButton clear_all = new JButton("clear all");
        clear_all.setFont(new Font("微软雅黑",Font.BOLD,15));
        clear_all.setFocusable(false);
        clear_all.addActionListener(e -> history_result.setText(""));

        history_result = new JTextArea();
        history_result.setFont(new Font("微软雅黑",Font.BOLD,20));
        history_result.setEditable(false);
        history_result.setLineWrap(true);

        JScrollPane history_scroll = new JScrollPane(history_result);
        historySpace.add(history_scroll, BorderLayout.CENTER);

        setSize(800,600);
        setMinimumSize(new Dimension(800,600));
        setLocationRelativeTo(null);//显示在屏幕中央
        setFocusable(true);
        setVisible(true);
    }
    private void handleKeyPress(KeyEvent e) {
        char keyChar = e.getKeyChar();
        int keyCode = e.getKeyCode();

        if (Character.isDigit(keyChar) || keyChar == '.') {
            result.append(String.valueOf(keyChar));
        } else {
            switch (keyCode) {
                case KeyEvent.VK_ADD:
                case KeyEvent.VK_PLUS:
                    result.append("+");
                    break;
                case KeyEvent.VK_MINUS:
                    result.append("-");
                    break;
                case KeyEvent.VK_MULTIPLY:
                    result.append("x");
                    break;
                case KeyEvent.VK_DIVIDE:
                    result.append("÷");
                    break;
                case KeyEvent.VK_ENTER:
                    getResult();  // 假设你有一个方法来计算结果
                    break;
                case KeyEvent.VK_BACK_SPACE://删除最后一个字符
                    String text = result.getText();
                    if (!text.isEmpty()) {
                        result.setText(text.substring(0, text.length() - 1));
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
                    result.setText("");  // 清空结果
                    break;
                    //添加阶乘
                case KeyEvent.VK_F1:
                    result.append("!");
                    break;
                //添加平方根
                case KeyEvent.VK_F2:
                    result.append("√");
                    break;
                default:
                    break;
            }
        }
    }
    public static void main(String[] args){
        MainWindow wnd = new MainWindow();
        String exp = "((1+2x4)÷3-2)÷(1-3)";
//        exp = "(9x5)";
//        ExpRes res = wnd.calculateExp(exp);
//        System.out.println(res.tag + " " + res.msg + " " + res.res);

        exp = "((-4)+3)x1";
        exp = "79-98x(-2)+(7x6÷5)-(-8)+0";
        ExpRes res = wnd.calculateExp(exp);
        System.out.println(res.to_string());
    }

    private void addNegative(){
        String exp = result.getText();

        //添加负号
        int ptr = exp.length()-1;
        while (ptr >= 0 && exp.charAt(ptr) >= 48 && exp.charAt(ptr) <= 57){
            ptr--;
        }
        String tmp = exp.substring(0, ptr+1) + "(" + "-" + exp.substring(ptr+1) + ")";
        result.setText(tmp);
    }

    private boolean checkLegality(char pre_char, char this_char){
        return switch (this_char) {
            case '(' -> pre_char == ' ' || pre_char == '(' || pre_char == '+' || pre_char == '-' ||
                    pre_char == 'x' || pre_char == '÷';
            case ')' -> pre_char == ')' || (pre_char >= '0' && pre_char <= '9');
            case '÷', 'x', '-', '+' -> pre_char == ')' || (pre_char >= '0' && pre_char <= '9');
            case '.' -> pre_char >= '0' && pre_char <= '9';
            case '!' -> pre_char >= '0' && pre_char <= '9'; // 确保阶乘符号只能跟在数字后面
            case '√' -> pre_char == ' ' || pre_char == '(' || pre_char == '+' || pre_char == '-' ||
                    pre_char == 'x' || pre_char == '÷';
            default -> pre_char == ' ' || pre_char == '.' || pre_char == '(' || pre_char == '+' || pre_char == '-' ||
                    pre_char == 'x' || pre_char == '÷' || (pre_char >= '0' && pre_char <= '9');
        };
    }

    private boolean checkPriority(char top, char c) {
        // 阶乘和平方根具有最高的优先级
        if (c == '!' || c == '√') {
            return false; // 允许压栈，因为没有比它们优先级更高的运算符
        }
        if (top == '!' || top == '√') {
            return true; // 栈顶是阶乘或平方根，应立即计算
        }
        // 乘除的优先级高于加减
        if ((c == 'x' || c == '÷') && (top == '+' || top == '-')) {
            return false; // 允许乘除运算符压栈
        }
        // 左括号总是允许压栈
        return top != '(';
        // 默认情况，保持运算符的顺序，即栈顶运算符先计算
    }

    private void getResult(){       //计算并显示结果
        String exp = result.getText();
        ExpRes expRes = calculateExp(exp);

        int res_int = (int)expRes.res;
        String res_str = "";
        if(expRes.tag.equals("OK")){
            if(expRes.res == 0) res_str += "0";
            else{
                if(expRes.res/res_int != 1){
                    res_str += expRes.res;
                }else{
                    res_str += res_int;//去掉整数结果后面的小数
                }
            }
        }

        System.out.println(expRes.tag + " " + expRes.msg + " " + expRes.res);

        if(expRes.tag.equals("OK")){
            result.setText(exp + " =\n" + res_str);
            String old_history = history_result.getText();
            history_result.setText(old_history + exp + " =\n" + res_str + "\n\n");
        }else if(expRes.tag.equals("ERROR")){
            result.setText(exp + "\n" + expRes.tag + " : " + expRes.msg);
        }

        done = true;
    }
    private ExpRes calculate() {
        if (operatorStack.isEmpty()) {
            return new ExpRes("ERROR", "运算符栈为空", 0);
        }

        char top = operatorStack.pop();
        double num1 = 0, res = 0;

        if (top == '!') {
            if (numberStack.isEmpty()) {
                return new ExpRes("ERROR", "数字栈为空", 0);
            }
            double num = numberStack.pop();
            if ((int) num != num || num < 0) {
                return new ExpRes("ERROR", "阶乘只对非负整数有效", 0);
            }
            res = factorial((int) num);
        } else if (top == '√') {
            if (numberStack.isEmpty()) {
                return new ExpRes("ERROR", "数字栈为空", 0);
            }
            double num = numberStack.pop();
            if (num < 0) {
                return new ExpRes("ERROR", "不能对负数取平方根", 0);
            }
            res = Math.sqrt(num);
        } else {
            if (numberStack.isEmpty()) {
                return new ExpRes("ERROR", "数字栈为空", 0);
            }
            double num2 = numberStack.pop();
            if (!numberStack.isEmpty()) {
                num1 = numberStack.pop();
            }

            switch (top) {
                case '+': res = num1 + num2; break;
                case '-': res = num1 - num2; break;
                case 'x': res = num1 * num2; break;
                case '÷':
                    if (num2 == 0) return new ExpRes("ERROR", "除数不能为0", 0);
                    res = num1 / num2;
                    break;
                default: return new ExpRes("ERROR", "未知运算符", 0);
            }
        }

        numberStack.push(res);
        return new ExpRes("OK", "", res);
    }
    private ExpRes handleOperator(char op, int index, String exp) {
        if (op == '!' || op == '√') {
            if (op == '!') {
                if (numberStack.isEmpty()) {
                    return new ExpRes("ERROR", "数字栈为空，无法计算阶乘", 0);
                }
                double num = numberStack.pop();
                if ((int) num != num || num < 0) {
                    return new ExpRes("ERROR", "阶乘只对非负整数有效", 0);
                }
                numberStack.push((double) factorial((int) num));
            } else if (op == '√') {
                if (index + 1 < exp.length() && Character.isDigit(exp.charAt(index + 1))) {
                    int j = index + 1;
                    while (j < exp.length() && (Character.isDigit(exp.charAt(j)) || exp.charAt(j) == '.')) j++;
                    String numStr = exp.substring(index + 1, j);
                    double num = Double.parseDouble(numStr);
                    if (num < 0) {
                        return new ExpRes("ERROR", "不能对负数取平方根", 0);
                    }
                    numberStack.push(Math.sqrt(num));
                    index = j - 1; // 更新索引到数字末尾
                } else {
                    return new ExpRes("ERROR", "平方根后缺少数字", 0);
                }
            }
        } else {
            while (!operatorStack.isEmpty() && !checkPriority(operatorStack.peek(), op)) {
                ExpRes res = calculate();
                if (res.tag.equals("ERROR")) {
                    return res;
                }
            }
            operatorStack.push(op);
        }
        return null;
    }

    private ExpRes calculateExp(String exp){
        operatorStack.clear();
        numberStack.clear();

        for(int i = 0; i < exp.length(); i++){
            char cur = exp.charAt(i);
            if(cur == '('){
                operatorStack.push(cur);
            } else if(cur == ')'){
                while(!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                    ExpRes res = calculate();
                    if(res.tag.equals("ERROR")){
                        return res;
                    }
                }
                operatorStack.pop(); // 弹出左括号
            } else if (cur == '!' || cur == '√') {
                handleOperator(cur, i,exp);
            } else if(cur == '+' || cur == '-' || cur == 'x' || cur == '÷'){
                if(cur == '-' && (i == 0 || exp.charAt(i-1) == '(')) { // 负号处理
                    int j = i + 1;
                    while(j < exp.length() && ((exp.charAt(j) >= '0' && exp.charAt(j) <= '9') || exp.charAt(j) == '.')) j++;
                    String num = exp.substring(i, j);
                    numberStack.push(Double.parseDouble(num));
                    i = j - 1;
                } else {
                    if(operatorStack.isEmpty() || operatorStack.peek() == '(') {
                        operatorStack.push(cur);
                    } else {
                        while(!operatorStack.isEmpty() && !checkPriority(operatorStack.peek(), cur)) {
                            ExpRes res = calculate();
                            if(res.tag.equals("ERROR")){
                                return res;
                            }
                        }
                        operatorStack.push(cur);
                    }
                }
            } else if(Character.isDigit(cur) || cur == '.'){
                int j = i;
                while(j + 1 < exp.length() && (Character.isDigit(exp.charAt(j+1)) || exp.charAt(j+1) == '.')) j++;
                String num = exp.substring(i, j+1);
                numberStack.push(Double.parseDouble(num));
                i = j;
            }
        }

        while(!operatorStack.isEmpty()) {
            ExpRes res = calculate();
            if(res.tag.equals("ERROR")){
                return res;
            }
        }
        if (!numberStack.isEmpty()) {
            return new ExpRes("OK", "", numberStack.pop());
        } else {
            return new ExpRes("ERROR", "表达式无效", 0);
        }
    }
    //按钮监视器

    class ButtonMonitor implements ActionListener{
        public void actionPerformed(ActionEvent e){

            playSound("calculator_finnal/src/lhx/click.wav");  // 添加这行代码来在按钮按下时播放声音
            JButton clickBtn = (JButton)e.getSource();
            for(int i = 0; i < btn_nums; i++){
                if(clickBtn == btn_sets[i]){
                    if(done) result.setText("");

                    if(i == 2){//按下C(Clear)
                        result.setText("");
                        done = true;
                    }else if(i == 16){//按下负号

                        String exp = result.getText();
                        if(!exp.isEmpty() && checkLegality(exp.charAt(exp.length()-1), '_')){
                            addNegative();
                            done = false;
                        }
                    }else if(i == 21){//按下等号
                        //System.out.println("Exp: " + result.getText());
                        getResult();
                    }
                    else if (i == 19) { // 阶乘按钮
                        String currentText = result.getText().trim();
                        if (!currentText.isEmpty() && Character.isDigit(currentText.charAt(currentText.length() - 1))) {
                            result.setText(currentText + "!");
                        }
                    }
                    else if (i == 20) { // 平方根按钮
                        calculateSquareRoot();
                    } else {
                        String exp = result.getText();
                        char pre_char = ' ';
                        if(!exp.isEmpty()){
                            pre_char = exp.charAt(exp.length()-1);
                        }
                        if(checkLegality(pre_char, btn_sets[i].getText().charAt(0))){
                            result.setText(exp + btn_sets[i].getText());
                            done = false;
                        }
                    }
                }
            }
        }
    }
    private void calculateSquareRoot() {
        try {
            double num = Double.parseDouble(result.getText().trim());
            if (num < 0) {
                throw new IllegalArgumentException("不能对负数取平方根");
            }
            double sqrtResult = Math.sqrt(num);
            result.setText("√" + num + " = " + sqrtResult);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "输入错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    class ExpRes{
        String tag;
        String msg;
        double res;

        public ExpRes(String tag, String msg, double res){
            this.tag = tag;
            this.msg = msg;
            this.res = res;
        }

        String to_string(){
            return this.tag + " " + this.msg + " " + this.res;
        }
    }
}
