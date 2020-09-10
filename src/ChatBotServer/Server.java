package ChatBotServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.EventHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Server {
	//������صĲ���,�˿�,�洢Socket���ӵļ���,ServerSocket����
    //�Լ��̳߳�
    private static final int PORT = 9898;
    private List<Socket> mList = new ArrayList<Socket>();
    private ServerSocket server = null;
    private ExecutorService myExecutorService = null;
    
    
    public static void main(String[] args) {
        new Server();
    }

    public Server()
    {
        try
        {
            server = new ServerSocket(PORT);
            //�����̳߳�
            myExecutorService = Executors.newCachedThreadPool();
            System.out.println("�����������...\n");
            Socket client = null;
            while(true)
            {
                client = server.accept();//��������
                mList.add(client);//�����̳߳�
                myExecutorService.execute(new Service(client));//����service
            }
            
        }catch(Exception e){e.printStackTrace();}
    }
    
    class Service implements Runnable
    {
        private Socket socket;//socket����
        private BufferedReader in = null;//������
        private String msg = "";
        private String que = "";
        
        public Service(Socket socket) {
            this.socket = socket;
            try
            {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));//����������
                 //msg = "�û�:" +this.socket.getInetAddress() + "~������������"  
                           // +"��ǰ��������:" +mList.size();  
                this.sendmsg();
            }catch(IOException e){e.printStackTrace();}
        }
        
        
        
        @Override
        public void run() {
            try{
                while(true)
                {
                    if(( que= in.readLine()) != null)
                    {
                        /*if(msg.equals("bye"))
                        {
                            System.out.println("~~~~~~~~~~~~~");
                            mList.remove(socket);
                            in.close();
                            msg = "�û�:" + socket.getInetAddress()  
                                    + "�˳�:" +"��ǰ��������:"+mList.size();  
                            socket.close();  
                            this.sendmsg();  
                            break;
                        }else{
                            msg = socket.getInetAddress() + "   ˵: " + msg;  
                            this.sendmsg(); 
                        }*/
                    	msg = machine(que);
                    	this.sendmsg();
                    }
                }
            }catch(Exception e){e.printStackTrace();}
        }
        
        //Ϊ�����Ϸ���˵�ÿ���ͻ��˷�����Ϣ
        public void sendmsg()
        {
            System.out.println(msg);
            int num = mList.size();
            for(int index = 0;index < num;index++)
            {
                Socket mSocket = mList.get(index);  
                PrintWriter pout = null;  
                try {  
                    pout = new PrintWriter(new BufferedWriter(  
                            new OutputStreamWriter(mSocket.getOutputStream(),"UTF-8")),true);  
                    pout.println(msg);  
                }catch (IOException e) {e.printStackTrace();}  
            }
        }
        
        private String machine(String quesiton) throws IOException {
            //��������ˣ���������
            String APIKEY = "";//���������������Կ
            String INFO = URLEncoder.encode(quesiton, "utf-8");//���������������
            String getURL = "http://www.tuling123.com/openapi/api?key=" + APIKEY + "&info=" + INFO;
            URL getUrl = new URL(getURL);
            HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
            connection.connect();

            // ȡ������������ʹ��Reader��ȡ
            BufferedReader reader = new BufferedReader(new InputStreamReader( connection.getInputStream(), "utf-8"));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            // �Ͽ�����
            connection.disconnect();
            String[] ss = new String[10];
            String s = sb.toString();
            String answer;
            ss = s.split(":");
            answer = ss[ss.length-1];
            answer = answer.substring(1,answer.length()-2);
            return answer;
        }
        
    }
}
