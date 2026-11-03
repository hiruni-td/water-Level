import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class WaterLevelObserver{
	public void update(int level,String id){
	}
}
class WaterLevelController{
	private int waterLevel = 60;
	private String deviceId = "A001";
	
	private ArrayList<WaterLevelObserver>observers = new ArrayList<>();
	
	public int getWaterLevel(){
		return waterLevel;
	}
	public String getDeviceId(){
		return deviceId;
	}
	public void addObserver(WaterLevelObserver observer){
		observers.add(observer);
	}
	public void setDeviceId(String id){
		if(id != null && !id.trim().isEmpty()){
			deviceId = id.trim();
			notifyObservers();
		}
	}
	public void changeLevel(int amount){
		int newLevel = waterLevel+amount;
		
		if(newLevel<0 || newLevel>100){
			JOptionPane.showMessageDialog(null,"Water level must be between 0 and 100!");
			return;
		}
		waterLevel=newLevel;
		notifyObservers();
	}
	public void notifyObservers(){
		for(WaterLevelObserver observer : observers){
			observer.update(waterLevel,deviceId);
		}
	}
}

class AlarmObserver extends WaterLevelObserver{
	private JLabel label;
	
	AlarmObserver(JLabel label){
		this.label = label;
	}
	public void update(int level,String id){
		if(level>50){
			label.setText("ALARM ON");
			label.setForeground(Color.RED);
		}else{
			label.setText("ALARM OFF");
			label.setForeground(Color.GREEN);
		}
	}
}
class MessageObserver extends WaterLevelObserver{
	private JLabel label;
	
	MessageObserver(JLabel label){
		this.label = label;
	}
	public void update(int level,String id){
		if(level>75){
			label.setText("DANGER");
			label.setForeground(Color.RED);
		}else{
			label.setText("NORMAL");
			label.setForeground(Color.GREEN);
		}
	}
}
class DeviceObserver extends WaterLevelObserver{
	private JLabel idLabel;
	private JLabel statusLabel;
	
	DeviceObserver(JLabel idLabel,JLabel statusLabel){
		this.idLabel = idLabel;
		this.statusLabel = statusLabel;
	}
	public void update(int level,String id){
		idLabel.setText("Device ID :"+id);
		
		if(level>75){
			statusLabel.setText("DEVICE ON");
			statusLabel.setForeground(Color.GREEN);
		}else{
			statusLabel.setText("DEVICE OFF");
			statusLabel.setForeground(Color.RED);
		}
	}
}
class NotificationObserver extends WaterLevelObserver{
	private JTextArea area;
	
	NotificationObserver(JTextArea area){
		this.area=area;
	}
	public void update(int level,String id){
		area.append("Device ID:"+id+"| Water Level:"+level+"%\n");
	}
}
class Example{
	private static WaterLevelController controller = new WaterLevelController();
	
	private static JLabel levelLabel;
	private static JProgressBar progressBar;
	private static JTextField deviceIdInput;
	
	public static void main(String[] args){
		SwingUtilities.invokeLater(()->{
			createControllerRoom();
			createAlarmWindow();
			createMessageWindow();
			createDeviceWindow();
			createNotificationWindow();
		});
	}
	private static void createControllerRoom(){
		JFrame frame = new JFrame("Controller Room");
		frame.setSize(350,250);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(5,1,5,5));
		
		JLabel levelLabel = new JLabel("",SwingConstants.CENTER);
		levelLabel.setFont(new Font("Sans-Serif",Font.BOLD,22));
		
		JProgressBar progressBar = new JProgressBar(0,100);
		progressBar.setStringPainted(true);
		
		JButton up = new JButton("Increase + 10");
		JButton down = new JButton("Decrease - 10");
		
		up.addActionListener(e-> controller.changeLevel(10));
		down.addActionListener(e-> controller.changeLevel(-10));
		
		frame.add(new JLabel("WATER LEVEL CONTROLLER ROOM",SwingConstants.CENTER));
		frame.add(levelLabel);
		frame.add(progressBar);
		frame.add(up);
		frame.add(down);
		
		frame.setLocation(30,100);
		frame.setVisible(true);
		
		controller.addObserver(new WaterLevelObserver(){
			@Override
			public void update(int level,String id){
				levelLabel.setText("Water Level:"+level+"%");
				progressBar.setValue(level);
				progressBar.setString(level+"%");
			}
		});
	}
	private static void createAlarmWindow(){
		JFrame frame = new JFrame("Alarm Window");
		frame.setSize(280,150);
		
		JLabel label = new JLabel("",SwingConstants.CENTER);
		label.setFont(new Font("Sans-Serif",Font.BOLD,25));
		
		frame.add(label);
		frame.setLocation(400,100);
		frame.setVisible(true);
		
		controller.addObserver(new AlarmObserver(label));
	}
	private static void createMessageWindow(){
		JFrame frame = new JFrame("Message Window");
		frame.setSize(320,150);
		
		JLabel label = new JLabel("",SwingConstants.CENTER);
		label.setFont(new Font("Sans-Serif",Font.BOLD,25));
		
		frame.add(label);
		frame.setLocation(400,300);
		frame.setVisible(true);
		
		controller.addObserver(new MessageObserver(label));
	}
	private static void createDeviceWindow(){
		JFrame frame = new JFrame("Device Window");
		frame.setSize(350,220);
		frame.setLayout(new GridLayout(4,1,5,5));
		
		JLabel idLabel = new JLabel("",SwingConstants.CENTER);
		deviceIdInput = new JTextField(controller.getDeviceId());
		
		JButton update = new JButton("Update Device ID");
		
		JLabel statusLabel = new JLabel("",SwingConstants.CENTER);
		
		update.addActionListener(e->{
			String id = deviceIdInput.getText().trim();
			
			if(id.isEmpty()){
				JOptionPane.showMessageDialog(frame,"Please enter a Device ID!");
				return;
			}
			controller.setDeviceId(id);
		});
		frame.add(idLabel);
		frame.add(statusLabel);
		frame.add(update);
		frame.add(deviceIdInput);
		
		frame.setLocation(700,300);
		frame.setVisible(true);
		
		controller.addObserver(new DeviceObserver(idLabel,statusLabel));
	}
	private static void createNotificationWindow(){
		JFrame frame = new JFrame("Notification Window");
		frame.setSize(350,250);
		
		JTextArea area = new JTextArea();
		area.setEditable(false);
		
		frame.add(new JScrollPane(area));
		frame.setLocation(700,100);
		frame.setVisible(true);
		
		controller.addObserver(new NotificationObserver(area));
	}
}
