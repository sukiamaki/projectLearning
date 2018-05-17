import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;

public class Server {
	private int port; // サーバの待ち受けポート
	private PrintWriter [] out; //データ送信用オブジェクト
	private Receiver [] receiver; //データ受信用オブジェクト
	
	private boolean [] online; //オンライン状態管理用配列	
	private int player_count = 0;	//入ってきたプレイヤの数をカウント
	
	

	//コンストラクタ
	public Server(int port) { //待ち受けポートを引数とする
		this.port = port; //待ち受けポートを渡す
		out = new PrintWriter [2]; //データ送信用オブジェクトを2クライアント分用意
		receiver = new Receiver [2]; //データ受信用オブジェクトを2クライアント分用意
		online = new boolean[2]; //オンライン状態管理用配列を用意
	}

	// データ受信用スレッド(内部クラス)
	class Receiver extends Thread {
		private InputStreamReader sisr; //受信データ用文字ストリーム
		private BufferedReader br; //文字ストリーム用のバッファ
		private int playerNo = 0;	//プレイヤを識別するための番号

		// 内部クラスReceiverのコンストラクタ
		Receiver (Socket socket, int playerNo){
			try{
				this.playerNo = playerNo;
				sisr = new InputStreamReader(socket.getInputStream());
				br = new BufferedReader(sisr);
			} catch (IOException e) {
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}
		
		// 内部クラス Receiverのメソッド
		public void run(){
			try{
				System.out.println(playerNo + "との接続に成功");
				onlineStatus(playerNo);
				
				while(true) {// データを受信し続ける
					String inputLine = br.readLine();//データを一行分読み込む
					String order = inputLine.substring(0,1);
					System.out.println(playerNo + "からメッセージ" + inputLine + "が届きました");
					
					if (inputLine != null){ //データを受信したら
						
						if(order.equals("1")) {	//手番情報のリクエストを受け付けたら
							sendColor(playerNo);
						}
						else if(order.equals("2")) {	//手番＋盤面情報の転送要求を受けたら
							forwardMessage(inputLine,playerNo);
						}
						else if(order.equals("3")){	//投了の情報を受けたら
							forwardMessage(inputLine,playerNo);
						}else {
							forwardMessage(inputLine,playerNo);
						}
					}
				}
			} catch (IOException e){ // 接続が切れたとき
				System.err.println("クライアントとの接続が切れました．ゲームを終了します");
				offlineStatus(playerNo);
				player_count=0;
				/*この後どうする？*/
			}
		}
		
		public void onlineStatus(int i) {	//オンラインに変更
			online[i]=true;
		}
		
		 public void offlineStatus(int i) {	//オフラインに変更
			online[i]=false;
		}
	}

	
	
	// メソッド

	public void acceptClient(){ //クライアントの接続(サーバの起動)
		try {
			System.out.println("サーバが起動しました．");
			ServerSocket ss = new ServerSocket(port); //サーバソケットを用意
			while (player_count < 2) {
				Socket socket = ss.accept(); //新規接続を受け付ける
				
				System.out.println(player_count+1 + "人目のクライアントからのログイン要請を受け付けました");
				
				out[player_count] = new PrintWriter(socket.getOutputStream(), true);//データ送信オブジェクトを用意
				receiver[player_count] = new Receiver(socket,player_count);//データ受信オブジェクト(スレッド)を用意
				receiver[player_count].start();	//データ送信オブジェクト(スレッド)を起動
				
				player_count++;
			}
		} catch (Exception e) {
			System.err.println("ソケット作成時にエラーが発生しました: " + e);
		}
	}
	
	public void printStatus(){ //クライアント接続状態の確認
	}
		
	public void sendColor(int playerNo){ //先手後手情報(白黒)の送信
		if(playerNo == 0) {
			out[playerNo].println("1black");//情報の送信
			out[playerNo].flush();
			
		}else if(playerNo == 1) {
			out[playerNo].println("1white");//情報の送信
			out[playerNo].flush();
			
		}
	}
	
	
	public void forwardMessage(String msg, int playerNo){ //操作情報の転送
		out[1-playerNo].println(msg);//情報の送信
		out[1-playerNo].flush();
	}
	
	public static void main(String[] args){ //main
		Server server = new Server(10000); //待ち受けポート10000番でサーバオブジェクトを準備
		server.acceptClient(); //クライアント受け入れを開始
		
		
	}


}
