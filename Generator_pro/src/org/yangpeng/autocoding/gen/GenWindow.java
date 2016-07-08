package org.yangpeng.autocoding.gen;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * 自动代码生成工具界面
 * @author yangpeng
 *
 */
public class GenWindow implements ActionListener {
    private Logger logger = Logger.getLogger(this.getClass());

    JFrame frame = new JFrame("代码自动生成工具v1.0");// 框架布局
    JLabel author = new JLabel("作者:杨鹏");
    Container con = new Container();//
    JLabel label1 = new JLabel("文件输出目录:");
    JTextField packagePath = new JTextField();// TextField 目录的路径
    JButton button1 = new JButton("...");// 选择

    JFileChooser jfc = new JFileChooser();// 文件选择器

    Choice jc = new Choice() ;

    // 数据库连接
    JLabel jLabel2 = new JLabel("SQL主机地址:") ;
    JTextField url = new JTextField("192.168.8.128:3306/goodscenter") ;

    // 用户名
    JLabel jLabel3 = new JLabel("用户名:") ;
    JTextField username = new JTextField("root") ;
    
    // 密码
    JLabel jLabel4 = new JLabel("密 码:") ;
    JPasswordField password = new JPasswordField("root") ;

    // 表名
    JLabel jLabel5 = new JLabel("表名称:") ;
    JTextField tableName = new JTextField() ;

    // 包名
    JLabel jLabel6 = new JLabel("包名:") ;
    JTextField packageName = new JTextField("com.camelot.dev") ;

    JList<String> tables = new JList<String>() ;


    JButton conBtn = new JButton("连接");//
    JButton ok = new JButton("生成");//
    JButton cancel = new JButton("取消");//
    JButton refresh = new JButton("重连") ;

    GenWindow() {
        jfc.setCurrentDirectory(new File("d://"));// 文件选择器的初始目录定为d盘

        double lx = Toolkit.getDefaultToolkit().getScreenSize().getWidth();

        double ly = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

        frame.setLocation(new Point((int) (lx / 2) - 150, (int) (ly / 2) - 150));// 设定窗口出现位置
        frame.setSize(430, 260);// 设定窗口大小
        //frame.setContentPane(tabPane);// 设置布局


        jLabel2.setBounds(10, 10, 90, 20);
        url.setBounds(95, 10, 220, 20);
        con.add(jLabel2);
        con.add(url);

        jLabel3.setBounds(10, 35, 90, 20);
        username.setBounds(95, 35, 220, 20);
        con.add(jLabel3);
        con.add(username);

        jLabel4.setBounds(10, 60, 90, 20);
        password.setBounds(95, 60, 220, 20);
        con.add(jLabel4);
        con.add(password);



        conBtn.setBounds(120, 110, 70, 20);
        //ok.setBounds(120, 170, 70, 20);
        cancel.setBounds(200, 110, 80, 20);
      //  button1.addActionListener(this); // 添加事件处理
        // ok.addActionListener(this); // 添加事件处理
        cancel.addActionListener(this);
        conBtn.addActionListener(this);
       // con.add(ok);
        con.add(cancel) ;
        con.add(conBtn) ;

        author.setBounds(350, 200, 80, 20);
        author.setForeground(Color.pink);
        author.setFont(new Font("Serif",Font.PLAIN,12));
        con.add(author) ;
        frame.setVisible(true);// 窗口可见
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 使能关闭窗口，结束程序
        frame.add(con) ;
    }
    /**
     * 时间监听的方法
     */
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        if (e.getSource().equals(button1)) {// 判断触发方法的按钮是哪个
            jfc.setFileSelectionMode(1);// 设定只能选择到文件夹
            int state = jfc.showOpenDialog(null);// 此句是打开文件选择器界面的触发语句
            if (state == 1) {
                return;
            } else {
                File f = jfc.getSelectedFile();// f为选择到的目录
                packagePath.setText(f.getAbsolutePath());
            }
        }
        if (e.getSource().equals(ok)) {
            // 自动生成代码
            String tableName = jc.getSelectedItem() ;
            gen(tableName) ;
        }
        if (e.getSource().equals(cancel)) {
            System.exit(0);
        }
        if(e.getSource().equals(conBtn)){
            if(validateInput("connection")){
                // 连接数据库
                loadTables();
            }
        }
        if(e.getSource().equals(refresh)){
            if(validateInput("connection")){
                // 连接数据库
                refresh() ;
            }
        }
    }

    /**
     * 刷新数据库连接
     * author yangpeng on 2016-06-30
     */
    public void refresh(){
        try {
            button1.removeActionListener(this); // 选这文件夹
            ok.removeActionListener(this);
            refresh.removeActionListener(this);
            con.remove(jc);
            jc = new Choice() ;
            jc.setBounds(95, 85, 220, 20);
            java.util.List<String> tables_ =  new Generator().getTabels(url.getText(),username.getText(),String.valueOf(password.getPassword())) ;
            String [] tablesArr = new String [tables_.size()] ;
            for (int i = 0; i < tables_.size() ; i++) {
                tablesArr[i] = tables_.get(i) ;
                jc.addItem(tables_.get(i));
            }
            ok.addActionListener(this);
            refresh.addActionListener(this);
            con.add(jc) ;
            frame.repaint();
        }catch (Exception e){

        }
    }

    /**
     * 加载数据库表
     * author yangpeng on 2016-06-29
     */
    public void loadTables(){
        try {
            java.util.List<String> tables_ =  new Generator().getTabels(url.getText(),username.getText(),String.valueOf(password.getPassword())) ;
            conBtn.removeActionListener(this);
            con.remove(conBtn); // 移除连接按钮
            String [] tablesArr = new String [tables_.size()] ;
            for (int i = 0; i < tables_.size() ; i++) {
                tablesArr[i] = tables_.get(i) ;
                jc.addItem(tables_.get(i));
            }
            // 选择表
            tables = new JList<>(tablesArr);
            jLabel5.setBounds(10, 85, 80, 20);
            jc.setBounds(95, 85, 220, 20);
            con.add(jLabel5);
            con.add(jc) ;

            // 填写包名
            jLabel6.setBounds(10, 110, 90, 20);
            packageName.setBounds(95, 110, 220, 20);
            con.add(jLabel6);
            con.add(packageName);

            // 选择文件
            label1.setBounds(10, 135, 90, 20);
            packagePath.setBounds(95, 135, 220, 20);
            button1.setBounds(310, 135, 50, 20);
            con.add(label1);
            con.add(packagePath);
            con.add(button1);

            // 按钮位置
            refresh.setBounds(40, 170, 70, 20);
            ok.setBounds(120, 170, 70, 20);
            cancel.setBounds(200, 170, 80, 20);
            button1.addActionListener(this); // 添加事件处理
            ok.addActionListener(this); // 添加事件处理
            refresh.addActionListener(this); // 刷新按钮

            con.add(refresh) ;
            con.add(ok);

            con.revalidate();
            frame.revalidate();
            frame.repaint();
        }catch (Exception e1){
            logger.info(e1.getMessage());
            JOptionPane.showMessageDialog(null, e1.getMessage());
            JOptionPane.showMessageDialog(null, "mysql connection is failed!");
        }
    }
    /**
     * 执行生成
     * author yangpeng on 2016-06-28
     */
    public void gen(String tableName){
        try {
            if(validateInput("gen")){
                Generator g = new Generator();
                g.gen(tableName,url.getText().trim(),username.getText().trim(),String.valueOf(password.getPassword()).trim(),packageName.getText().trim(),packagePath.getText().trim());
                JOptionPane.showMessageDialog(null, "代码已经生成完成！");
            }
        }catch (Exception e){
            logger.debug("error",e);
            JOptionPane.showMessageDialog(null, e.getMessage());
            JOptionPane.showMessageDialog(null, "mysql connection is failed!");
        }
    }

    /**
     * 输入校验
     * author yangpeng on 2016-06-28
     */
    public Boolean validateInput(String operation){
        if(!StringUtils.isNotBlank(url.getText())){
            JOptionPane.showMessageDialog(null, "数据库url不能为空！");
            return false ;
        }
        if(!StringUtils.isNotBlank(username.getText())){
            JOptionPane.showMessageDialog(null, "用户名不能为空！");
            return false ;
        }
        if(!StringUtils.isNotBlank(String.valueOf(password.getPassword()))){
            JOptionPane.showMessageDialog(null, "密码不能为空！");
            return false ;
        }
        if("gen".equals(operation)){
            if(!StringUtils.isNotBlank(packagePath.getText())){
                JOptionPane.showMessageDialog(null, "文件输出路径不能为空！");
                return false ;
            }else{
                File file = new File(packagePath.getText()) ;
                if(!file.exists()){
                    JOptionPane.showMessageDialog(null, "文件路径出错！");
                    return false ;
                }
            }
            if(!StringUtils.isNotBlank(jc.getSelectedItem())){
                JOptionPane.showMessageDialog(null, "表名不能为空！");
                return false ;
            }
            if(!StringUtils.isNotBlank(packageName.getText())){
                JOptionPane.showMessageDialog(null, "类包名不能为空！");
                return false ;
            }
        }
        return true ;
    }

    public static void main(String[] args) {
        new GenWindow();
    }
}