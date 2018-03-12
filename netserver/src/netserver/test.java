package netserver;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField; 

public class test {


	public static void main(String[] args) {   
		
		Runnable runs = new Runnable() {
			
			@Override
			public void run() {
				new HeartBeatServer(8080).start();
			}
		};
		
		Thread thread = new Thread(runs);
		thread.start();
		
        // 创建 JFrame 实例
        JFrame frame = new JFrame("发送消息");
        // Setting the width and height of frame
        frame.setSize(650, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /* 创建面板，这个类似于 HTML 的 div 标签
         * 我们可以创建多个面板并在 JFrame 中指定位置
         * 面板中我们可以添加文本字段，按钮及其他组件。
         */
        JPanel panel = new JPanel();    
        // 添加面板
        frame.add(panel);
        /* 
         * 调用用户定义的方法并添加组件到面板
         */
        placeComponents(panel);

        // 设置界面可见
        frame.setVisible(true);
    }
    
    
    private static void placeComponents(JPanel panel) {
    	
        /* 布局部分我们这边不多做介绍
         * 这边设置布局为 null
         */
        panel.setLayout(null);

        // 创建 JLabel
        JLabel userLabel = new JLabel("发送消息:");
        /* 这个方法定义了组件的位置。
         * setBounds(x, y, width, height)
         * x 和 y 指定左上角的新位置，由 width 和 height 指定新的大小。
         */
        userLabel.setBounds(10,20,80,25);
        panel.add(userLabel);

        /* 
         * 创建文本域用于用户输入
         */
        JTextField userText = new JTextField(20);
        userText.setBounds(100,20,200,300);
        panel.add(userText);


        // 创建登录按钮
        JButton loginButton = new JButton("发送");
        loginButton.setBounds(10, 80, 80, 25);
        panel.add(loginButton);
        
        loginButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				PlayerManager.sendMessage(userText.getText());
				
			}
		});
        
    }
}
