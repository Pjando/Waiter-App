package group4.waiterapp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.util.Log;

import shared.Command;

import shared.ServerAddress;

/**
 * @author Pavan Jando
 *
 * Class used to connect to the server.
 */
class Connection {

  /**
   * Thread listener used to detect incoming packets from the server.
   */
  private static Thread listener;
  /**
   * Listener used to send commands to the server.
   */
  private static Thread dataProcessor;
  /**
   * The ip of the server.
   */
  private static String serverIP;
  /**
   * Socket used to send packets from.
   */
  private static DatagramSocket c;
  /**
   * Singleton object for storing data.
   */
  private Exchange sharedData;

  public static Boolean connectionMade = false;

  /**
   * De-serialise and obj sent from the server.
   * @param obj the serialised object received from the server.
   * @return the de-serialised object from the server.
   * @throws Exception throws and exception.
   */
  static Object deserialise(byte[] obj) throws Exception {
    // Convert the packet to an object
    ByteArrayInputStream in = new ByteArrayInputStream(obj);
    ObjectInputStream is = new ObjectInputStream(in);
    return is.readObject();
  }

  /**
   * Serialises objects to send to the server.
   * @param obj the object to be serialised.
   * @return the serialised object.
   * @throws Exception throws and exception.
   */
  static byte[] serialise(Object obj) throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ObjectOutputStream os = new ObjectOutputStream(out);
    os.writeObject(obj);
    return out.toByteArray();
  }

  /**
   * Send the serialised object in a datagram packet to the server.
   * @param data serialised object.
   * @throws Exception throws an exception.
   */
  static void sendToServer(Object data) throws Exception {
    byte[] sendData = serialise(data);
    DatagramPacket sendPacket = new DatagramPacket(sendData,
        sendData.length,
        InetAddress.getByName(serverIP),
        48842);
    c.send(sendPacket);
  }

  /**
   * Creates the thread that establishes a connection to server and listens for incoming packets.
   * Creates the thread that sends commands to the sever.
   */
  void threadListen() {
    serverIP = "255.255.255.255";
    String debugServerIp = "10.2.112.172";
    serverIP = debugServerIp;
    sharedData = Exchange.getInstance();
    try {
      listener = new Thread("Listener") {
        public void run() {
          try {

            serverIP = new ServerAddress().bruteForce("10.2.0.0");

            c = new DatagramSocket();
            c.setBroadcast(true);

            String test = "DISCOVER_SERVER_REQUEST:waiter_1:";
            byte[] sendData = serialise(test);

            try {
              DatagramPacket sendPacket = new DatagramPacket(sendData,
                  sendData.length, InetAddress.getByName(serverIP), 48842);
              c.send(sendPacket);

              System.out.println("Request packet sent");
            } catch (Exception e) {
              e.printStackTrace();
            }

            while (true) {
              //Wait for a response
              byte[] recvBuf = new byte[15000];
              DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
              c.receive(receivePacket);

              Object dataObj = deserialise(receivePacket.getData());

              if (dataObj.getClass().getName().equals("java.lang.String")) {
                String data = (String) dataObj;
                if (data.equals("RECIVED_REQUEST")) {
                  System.out.println("Connected to Server");
                  connectionMade = true;
                  serverIP = receivePacket.getAddress().getHostAddress();
                }
              } else if (dataObj.getClass().getName().equals("shared.Command")) {
                Command cmd = (Command) dataObj;
                if (cmd.getCommand().equals("RECIVE_TABLES")) {
                  Log.d("Tables", "HERE");
                  sharedData.setTables(cmd.getTables());
                } else if (cmd.getCommand().equals("VIEW_MENU")) {
                  Log.d("MENU", "HERE");
                  sharedData.setMenu(cmd.getItems());
                } else if (cmd.getCommand().equals("CONFIRM_ORDER")) {
                  Log.d("CONFIRM", "HERE");
                  sharedData.setOrder(cmd.getTableNumber(), cmd.getItems(), cmd.getTimeOfOrder());
                  Log.d("ITEMS", String.valueOf(cmd.getTableNumber()));
                } else if (cmd.getCommand().equals("ORDER_READY") || cmd.getCommand().equals("ORDER_COOKING") || cmd.getCommand().equals("ORDER_RECEIVED")) {
                  Log.d("STATUS", cmd.getCommand());
                  sharedData.setTableStatus(cmd);
                } else if (cmd.getCommand().equals("NOTIFY_WAITER")) {
                  Log.d("NOTIFY", cmd.getCommand());
                  sharedData.setHelp(cmd.getTableNumber(), true);
                } else if (cmd.getCommand().equals("PAID")) {
                  sharedData.setPaid(cmd.getTableNumber(), true);
                  Log.d("PAID", "PAID");
                }
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      };
      dataProcessor = new Thread("Data processor") {
        public void run() {
          while (true) {
            if (sharedData.getCommandQueue().size() == 0) {
              yield();
            } else {
              try {
                Command cmd = sharedData.getCommandQueue().poll();
                sendToServer(cmd);
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          }
        }
      };
      listener.start();
      dataProcessor.start();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
