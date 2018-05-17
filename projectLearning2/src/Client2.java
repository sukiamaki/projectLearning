//パッケージのインポート
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class Client2 extends JFrame implements MouseListener  {
	
	private JButton buttonArray[];//オセロ盤用のボタン配列
	private JButton stop, pass; //停止、スキップ用ボタン
	
	private JLabel myIDLabel; //自分のID表示用ラベル
	private JLabel oppIDLabel; //対戦相手のID表示用ラベル
	
	private JLabel myTimeLabel; //自分の残り時間表示用ラベル
	private JLabel oppTimeLabel; //対戦相手の残り時間表示用ラベル
	
	private JLabel colorLabel; // 色表示用ラベル
	private JLabel turnLabel; // 手番表示用ラベル
	
	private Container c; // コンテナ
	private ImageIcon blackIcon, whiteIcon, boardIcon; //アイコン
	
	private PrintWriter out;//データ送信用オブジェクト
	private Receiver receiver; //データ受信用オブジェクト
	
	private Othello game; //Othelloオブジェクト
	private Player player; //Playerオブジェクト

	// コンストラクタ
	public Client2(Othello game, Player player) {
		
		this.game = game; //引数のOthelloオブジェクトを渡す
		this.player = player; //引数のOthelloオブジェクトを渡す
		
		int row = game.getRow(); //getRowメソッドによりオセロ盤の縦横マスの数を取得
		String [] grids = game.getGrids(); //getGridメソッドにより局面情報を取得

		
		//ウィンドウ設定
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じる場合の処理
		setTitle("オセロゲ〜ム");//ウィンドウのタイトル
		int width = row * 45;
		int height =  70 + row * 45 + 150;
		setSize(width ,height);//ウィンドウのサイズを設定
		
		c = getContentPane();//フレームのペインを取得
		c.setLayout(null);
		
		
		//アイコン設定(画像ファイルをアイコンとして使う)
		whiteIcon = new ImageIcon("White.jpg");
		blackIcon = new ImageIcon("Black.jpg");
		boardIcon = new ImageIcon("GreenFrame.jpg");
		

		//ID表示用ラベル
		myIDLabel = new JLabel(player.getName());//ID情報を表示するためのラベルを作成
		myIDLabel.setHorizontalAlignment(JLabel.CENTER);
		myIDLabel.setBounds(0 ,5 , width/2, 30);//境界を設定
		c.add(myIDLabel);//色表示用ラベルをペインに貼り付け
		
		oppIDLabel = new JLabel("banana");//ID情報を表示するためのラベルを作成
		oppIDLabel.setHorizontalAlignment(JLabel.CENTER);
		oppIDLabel.setBounds(width/2 ,5 ,width/2 , 30);//境界を設定
		c.add(oppIDLabel);//色表示用ラベルをペインに貼り付け
		
		
		//残り時間表示用ラベル
		myTimeLabel = new JLabel("30:00");//残り時間情報を表示するためのラベルを作成
		myTimeLabel.setHorizontalAlignment(JLabel.CENTER);
		myTimeLabel.setBounds(0 ,35 ,width/2 , 30);//境界を設定
		c.add(myTimeLabel);//手番情報ラベルをペインに貼り付け
		
		oppTimeLabel = new JLabel("30:00");//残り時間情報を表示するためのラベルを作成
		oppTimeLabel.setHorizontalAlignment(JLabel.CENTER);
		oppTimeLabel.setBounds(width/2 , 35 ,width/2 , 30);//境界を設定
		c.add(oppTimeLabel);//手番情報ラベルをペインに貼り付け
		
		
		//オセロ盤の生成
		buttonArray = new JButton[(row+2) * (row+2)];//ボタンの配列を作成
		int count =0;	//見える部分のボタンのno
		
		for(int k=0; k<(row+2); k++) {
			buttonArray[k] = new JButton(); 
		}
		
		for(int i = 1 ; i <= row ; i++){
			for(int j=1; j<=row; j++) {
				
				if(grids[i*(row+2) + j].equals("black")){ buttonArray[i*(row+2) + j] = new JButton(blackIcon);}//盤面状態に応じたアイコンを設定
				if(grids[i*(row+2) + j].equals("white")){ buttonArray[i*(row+2) + j] = new JButton(whiteIcon);}//盤面状態に応じたアイコンを設定
				if(grids[i*(row+2) + j].equals("board")){ buttonArray[i*(row+2) + j] = new JButton(boardIcon);}//盤面状態に応じたアイコンを設定
				c.add(buttonArray[i*(row+2) + j]);//ボタンの配列をペインに貼り付け
						
				// ボタンを配置する
				int x = (count % row) * 45;
				int y = (int) (count / row) * 45;
				buttonArray[i*(row+2) + j].setBounds(x, 70 + y, 45, 45);//ボタンの大きさと位置を設定する．
				buttonArray[i*(row+2) + j].addMouseListener(this);//マウス操作を認識できるようにする
				buttonArray[i*(row+2) + j].setActionCommand(Integer.toString(i*(row+2) + j));//ボタンを識別するための名前(番号)を付加する
				count++;
			}
		}

		
		//終了ボタン
		stop = new JButton("終了");//終了ボタンを作成
		c.add(stop); //終了ボタンをペインに貼り付け
		stop.setBounds(0, 70 + row * 45 + 10, width/ 2, 30);//終了ボタンの境界を設定
		stop.addMouseListener(this);//マウス操作を認識できるようにする
		stop.setActionCommand("stop");//ボタンを識別するための名前を付加する
		
		//パスボタン
		pass = new JButton("パス");//パスボタンを作成
		c.add(pass); //パスボタンをペインに貼り付け
		pass.setBounds(width/ 2,70 + row * 45 + 10, width/ 2, 30);//パスボタンの境界を設定
		pass.addMouseListener(this);//マウス操作を認識できるようにする
		pass.setActionCommand("pass");//ボタンを識別するための名前を付加する
		
		//色表示用ラベル
		colorLabel = new JLabel("色は未定です");//色情報を表示するためのラベルを作成
		colorLabel.setBounds(10,70 + row * 45 + 50 , row * 45 + 10, 30);//境界を設定
		c.add(colorLabel);//色表示用ラベルをペインに貼り付け
		
		//手番表示用ラベル
		turnLabel = new JLabel("手番は未定です");//手番情報を表示するためのラベルを作成
		turnLabel.setBounds(10,70 + row * 45 + 80, row * 45 + 10, 30);//境界を設定
		c.add(turnLabel);//手番情報ラベルをペインに貼り付け
	}

	
	// メソッド	
	////////////////*データ受信用スレッド(内部クラス)*///////////////
	class Receiver extends Thread {
		private InputStreamReader sisr; //受信データ用文字ストリーム
		private BufferedReader br; //文字ストリーム用のバッファ

		// 内部クラスReceiverのコンストラクタ
		Receiver (Socket socket){
			try{
				sisr = new InputStreamReader(socket.getInputStream()); //受信したバイトデータを文字ストリームに
				br = new BufferedReader(sisr);//文字ストリームをバッファリングする
			} catch (IOException e) {
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}
		
		// 内部クラス Receiverのメソッド
		public void run(){
			try{
				while(true) {//データを受信し続ける
					String inputLine = br.readLine();//受信データを一行分読み込む
					String order = inputLine.substring(0, 1);
					
					if (inputLine != null){//データを受信したら
						System.out.println("データ" + inputLine + "を受信しました");
						
						if(order.equals("1")) { //先手後手情報の設定
							colorLabel_draw(inputLine.substring(1));
						}else if(order.equals("2")) {
							receiveData(inputLine.substring(1));
							board_draw(game.getGrids());
							turnLabel_draw();
						}
						else {
							receiveMessage(inputLine);//データ受信用メソッドを呼び出す
						}
					}
				}
			} catch (IOException e){
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}
	}
	
	
	
	///////////////////*サーバに接続*///////////////////////////////
	public void connectServer(String ipAddress, int port){	
		Socket socket = null;
		try {
			socket = new Socket(ipAddress, port); //サーバ(ipAddress, port)に接続
			
			System.out.println("プレイヤがサーバに接続を試みました");
			out = new PrintWriter(socket.getOutputStream(), true); //データ送信用オブジェクトの用意
			receiver = new Receiver(socket); //受信用オブジェクトの準備
			receiver.start();//受信用オブジェクト(スレッド)起動
			System.out.println("サーバ接続が成功しました");
			
		} catch (UnknownHostException e) {
			System.err.println("ホストのIPアドレスが判定できません: " + e);
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("サーバ接続時にエラーが発生しました: " + e);
			System.exit(-1);
		}
		
		System.out.println("サーバとの接続工程を終了しました");
	}
		
	
	
	///////////////////*メッセージの受信表示*//////////////////////
	public void receiveMessage(String msg){	
		System.out.println("サーバからメッセージ " + msg + " を受信しました"); //テスト用標準出力	
	}
	
	
	//////////////////*データの受信表示*//////////////////////
	public void receiveData(String msg) {
		/*はじめ1文字が手番情報、残りがひっくり返った石のgrids位置*/
		
		//手番の取得
		if(msg.substring(0, 1).equals("0")) {
			game.setTurn("black");
		}else {
			game.setTurn("white");
		}
		
		//局面の変化情報の受信
		String changeStone = msg.substring(1);	//ひっくり返った石のgrids位置のみ抽出、位置はカンマで区切られている
		int length = changeStone.length();
		int change;	//ひっくり返った石のgrids位置
		int start;
		int last=0;
		
		start = last+1;
		last = changeStone.indexOf(",", start);	//カンマの位置を検索

		while(last != -1){	//カンマがなくなるまで検索を続ける
			change = Integer.parseInt(changeStone.substring(start,last));//カンマの間を抽出
			game.setGrids(change, game.changeColor(game.getTurn()));//色を変える

			start = last+1; 
			last = changeStone.indexOf(",",start);	//カンマの位置を検索
		}
				
		change = Integer.parseInt(changeStone.substring(start,length));	//カンマの間を抽出
		game.setGrids(change, game.changeColor(game.getTurn())); //色を変える
		
		
	}
	
	
	
	
	///////////////////*メッセージの送信*//////////////////////
	public void sendMessage(String msg){	// サーバに操作情報を送信
		out.println(msg);//送信データをバッファに書き出す
		out.flush();//送信データを送る
		System.out.println("サーバにメッセージ" +msg+ "を送信しました"); //テスト標準出力
	}
	
	
	///////////////////*データの送信*//////////////////////
	public void sendData() {
		StringBuilder sb = new StringBuilder();

		//オーダー
		sb.append("2");
		
		//手番情報
		if(game.getTurn().equals("black")) {
		sb.append("0");
		}else {
		sb.append("1");
		}
		
		sb.append(game.getChangeStone());
		
		String msg = new String(sb);
		out.println(msg);//送信データをバッファに書き出す
		out.flush();//送信データを送る
		System.out.println("サーバにデータ" + msg + "を送信しました"); //テスト標準出力
	}
	
	
	
	
  	////////////////*マウスクリック時の処理*///////////////////
	public void mouseClicked(MouseEvent e) {
		JButton theButton = (JButton)e.getComponent();//クリックしたオブジェクトを得る．キャストを忘れずに
		String command = theButton.getActionCommand();//ボタンの名前を取り出す
		
		acceptOperation(command);	//データの受け入れ作業
	}
	
	public void mouseEntered(MouseEvent e) {}//マウスがオブジェクトに入ったときの処理
	public void mouseExited(MouseEvent e) {}//マウスがオブジェクトから出たときの処理
	public void mousePressed(MouseEvent e) {}//マウスでオブジェクトを押したときの処理
	public void mouseReleased(MouseEvent e) {}//マウスで押していたオブジェクトを離したときの処理
	
	
	
	///////////////////*プレイヤの操作受け付け*//////////////////////
	public void acceptOperation(String command){	
		//パスを選択した時
		if(command.equals("pass")) {		
			/*どうする？*/
		}
		//投了を選択した時
		else if(command.equals("stop")) {
			/*どうする？*/
		}
		//石を置こうとした時
		else {	
			if(game.getTurn().equals(player.getColor())) {	//自分のターンであれば
				
				int theButton_num = Integer.parseInt(command);	//ボタンの位置情報をintに変換
				if(game.putStone(theButton_num,game.getTurn(), true) == true) {	//石が置けるかの確認と盤面情報の変更
					board_draw(game.getGrids());	//画面に描画
					game.setTurn(game.changeColor(game.getTurn()));	//石を置くのに成功したら手番変更
					turnLabel_draw();
					sendData(); //操作情報の送信
				}
				
			}
		}
	
	}
	
	
	
	//////////////////*盤面の描画*//////////////////////////////
	public void board_draw(String[] grids) {
	
		for(int i = 1 ; i <= game.getRow() ; i++){
			for(int j=1; j<=game.getRow(); j++) {
				
				if(grids[i*(game.getRow()+2) + j].equals("black")){ buttonArray[i*(game.getRow()+2) + j].setIcon(blackIcon);}//盤面状態に応じたアイコンを設定	
				if(grids[i*(game.getRow()+2) + j].equals("white")){ buttonArray[i*(game.getRow()+2) + j].setIcon(whiteIcon);}//盤面状態に応じたアイコンを設定
				if(grids[i*(game.getRow()+2) + j].equals("board")){ buttonArray[i*(game.getRow()+2) + j].setIcon(boardIcon);}//盤面状態に応じたアイコンを設定
			}
		}
	}
	
	
	/////////////*先手後手情報の描画*//////////////////////////
	public void colorLabel_draw(String color) {
		player.setColor(color);
		colorLabel.setText("色は" + player.getColor() + "です");
	}
	
	/////////////*手番情報の描画*//////////////////////////
	public void turnLabel_draw() {
		turnLabel.setText("手番は" + game.getTurn() + "です");
	}

	
	
	
	
	
	
	//main
	public static void main(String args[]){ 
		Player player = new Player(); //プレイヤオブジェクトの用意
		Othello game = new Othello(); //オセロオブジェクトを用意
		
		Client2 oclient = new Client2(game, player);
		oclient.connectServer("localhost", 10000);		
		
		oclient.sendMessage("1先手後手情報の送信要求"); //先手後手情報の送信要求
		/*先手後手情報の受信*/
		/*先手後手情報情報の描画*/
		oclient.setVisible(true);
		oclient.turnLabel_draw(); //手番情報の描画
		
		
		/*ボタン操作*/
		/*操作情報の送信*/
		
				
		
	}
	
}
