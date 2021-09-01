# MYZHIBEI Chat Room
https://github.com/myzhibei/MYZHIBEI_ChatRoom

Java聊天室系统

POWERD BY MYZHIBEI

# 一、   设计目的

1．多客户端模式下，实现客户与客户的单独通信，要求信息通过服务器中转

2．端到端的通信，实现并行通信模式（不再是你说一句，我说一句，一端的信息发送不受另一端的影响）

3．实现端到端的文件传输

4．添加图形界面（选做）

# 二、   系统设计

## 1.    设计原理与思想

打开服务端后，服务端首先建立SocketServer，开始监听，一旦有客户连接便创立一个新的ServerThread线程用于与之通信，将所有ServerThread保存在一个CopyOnWriteArrayList中。

打开客户端后首先输入用户姓名作为用户标识，并通过Socket连接服务器端，服务器端TCP/IP进行三次握手后建立连接，服务端建立一个对应的Socket用于通信，客户随后建立发送线程和接收线程两个独立线程以能够同时发送和接收，然后客户端发送用户名到服务端，服务端将其保存在与之对应的ServerThread线程类中，用用户姓名作为标识，服务端将所有已存在客户端用户名发送至此客户端同时将该用户名发送至所有已存在客户端中，已存在客户端用户列表添加此用户名，该客户端用户列表添加所有已存在用户名以保持所有客户端中用户列表同步。

客户端选择发送对象（可以是个人也可以是全体成员）后，当客户端点击SendMessage按钮后，客户发送端读取输入框JTextArea中的文字并在文字前加上“@发送对象用户名：”，一并写入UTF-8编码的字符流，通过Socket套接字将其发送至服务器，服务器对消息用“@”和“：”进行截取，取出发送对象的用户名，对存有所有ServerThread进行遍历，匹配到对应用户名则发送消息来源端用户名加上消息到对应用户名处，如果是全体成员则直接发送，将字符流转发至客户接收端，客户接收端从套接字中读取字节流，并将其显示在显示框中。

当客户端点击SendFile按钮后，弹出选择文件框进行文件选择，然后发送“file@发送对象用户名:文件名”至服务端（随后客户端将文件字节流发送至服务端）服务端同样截取用户名然后遍历发送此消息，客户端接收到“file”的信息后弹出是否接收文件对话框，确认接收后弹出选择文件保存窗口，选择路径及文件名后开始接收字节流并将其输出到文件输出流中完成文件接收。

## 2.    总体设计

Server端：

MultiTalkServer类为服务端主线程，用于监听客户端连接；ServerThread类为与每一个客户端对应的服务端线程，监听到一个客户端连接便会实例化一个此线程。

Client端：

TalkClient类为客户端主线程用于创建客户端窗口以及初始化客户端各项设置；send类为发送信息线程，由TalkClient类创建运行，一旦点击SendMessage或者SendFile便会调用此线程；sendFile为发送文件类，由send类创建，点击SendFile后会由send类进行实例化；Receive类为接收信息线程，由TalkClient类创建运行，一旦检测到信息后便将其打印到信息显示框中；receiveFile类为接收文件类，由Receive线程实例化，一旦检测到信息为文件信息便实例化此类；

## 3.    详细设计

### ①TalkClient类：

创建客户端窗口（JFrame），与服务端进行连接创建Socket，弹出输入用户名对话框，输入用户名后创建send和receive线程，获取内容容器ct（Container），添加接收信息框rec（JTextArea嵌套JScrollPane）、发送信息框msg（JTextArea嵌套JScrollPane）、用户列表Contacts（JList嵌套JScrollPane）、发送对象用户名显示标签ToNm（JLabel）发送信息按钮sendM（JButton）和发送文件按钮sendF（JButton）至ct，对每一个组件设置显示文字、颜色、字体、大小等；对于用户列表添加监听，默认选择第一项全体成员，用户选择（点击）用户后更新对象名标签（JLabel）为发送对象名；创建关闭函数用于用户自主关闭，切断Socket连接；

### ②Send类：

实现Runnable接口作为独立线程运行，TalkClient类创建此线程时传递Socket和用户名，获取Socket的DataOutputStream，使用writeUTF方法创立发送字符流方法send，创立后首先将用户名发送至服务端，在run方法中对TalkClient窗体中SendMessage按钮和SendFile按钮添加监听，用户点击SendMessage后获取发送信息框内容并获取“发送对象用户名显示标签ToNm”的显示文字添加“@”和“：”进行标识后发送至服务端；用户点击SendFile后直接实例化一个sendFile对象；

### ③sendFile类：

首先弹出选择文件框（JFileChooser），选择确定后获取该文件（File）及该文件路径和文件名，使用writeUTF方法发送“file@‘发送对象用户名显示标签ToNm”的显示文字’：文件名”至服务端，随后获取该文件的文件输入流（FileInputStream嵌套DataInputStream），获取文件大小后以每次发送1KB进行循环发送字节流至服务端，发送完毕后关闭文件输入流；

### ④Receive类：

实现Runnable接口作为独立线程运行，TalkClient类创建此线程时传递Socket，获取Socket的DataInputStream，在run方法中使用readUTF方法直接接受字符流并显示在TalkClient窗体的消息显示框中，实现信息接收，如果消息以“file” 为开头，则实例化一个receiveFile对象，如果消息为“---Close chat room---”则关闭客户端，如果消息含有“has joined the chat room!”或“has quit the chat room!”则取出用户名并添加到TalkClient的用户列表中或将用户名移除；

### ⑤receiveFile类：

首先弹出是否接收文件对话框（showOptionDialog）询问是否接收文件，点击是后弹出文件保存框（showSaveDialog），选定文件保存路径及文件名后获取文件输出流（FileOutputStream嵌套DataOutputStream），以每次1KB循环接收（read）字节流并写入（write）文件，完成文件接收后关闭文件输出流，点击否后清空接收流中内容并在消息接收框中显示已拒绝接受文件；

### ⑥MultiTalkServer类:

在某一端口创建ServerSocket，开始循环监听（accept），一有客户端连接便创建一个新ServerThread线程，并将所有创建的ServerThread线程保存在一个CopyOnWriteArrayList<ServerThread>中，创建一个关闭方法用于用户自主关闭服务器；

### ⑦ServerThread类：

创建该线程时传递Socket参数，获取该Socket的输入输出流dis、dos（getInputStream()、getOutputStream()），并以此创建信息接收方法receive和信息发送方法send，首先接收客户端发送来的客户端用户姓名并将该用户名保存为该线程的一个成员属性；创建私发（sendSomeone）和群发（sendEveryone）两个方法，两个方法均有四个参数，分别为信息文本字符串、文件字节、文件字节长度、是否为文件，sendSomeone方法首先取出发送对象用户名，若为全体成员（all）则调用sendEveryone方法，若不是则直接遍历所有服务端线程寻找匹配用户名后发送该信息，如果是文件则调用sendFile方法，sendEveryone方法与sendSomeone相同，区别仅仅在于此方法在遍历所有服务端线程时不判断用户名是否匹配；sendFile方法直接使用dos.write方法发送字节流；receiveFile方法使用dis.read方法以每次1023字节循环接收客户端发来的文件字节流并调用sendSomeone方法发送至目标对象客户端；在线程run方法中循环监听（receive方法），如果消息含有“Close chat room”则给所有客户端发送关闭指令（sendEveryone）并调用MultiTalkServer的关闭方法关闭服务器，如果消息为“@”开头则调用sendSomeone方法发送该信息，若为“file”开头即发送文件信息则先给发送对象发送该信息然后调用接收文件receiveFile方法；receive方法在客户端关闭后会出现接收异常，此时中断run方法中的监听循环并调用sendEveryone方法告知该客户端已退出聊天室，然后关闭该线程并将其从CopyOnWriteArrayList<ServerThread>中移除。

 

# 三、   系统测试

## 服务端：

首先启用服务端，在有客户端连接后在控制台输出客户端用户连接用户名，在控制台输出各种状态信息，比如接收文件等。

 

## 客户端：

1.  启动客户端后首先弹出输入用户名框，在用户列表栏默认选择全体成员。

![img](https://gitee.com/myzhibei/img/raw/master/6f919e4862452120a5e4d4f65127da16.png)

2.    
    点击确认后将自己添加到用户列表

再开启另外两个用户端，同样输入用户名后更新所有客户端的用户列表

![img](https://gitee.com/myzhibei/img/raw/master/7af74cc876e34a0dd986a778e6d8623b.png)

3.  Pengmy输入文字后点击Send
    Message按钮后自己客户端显示消息记录，在其他所有客户端则显示Pengmy\@all:____;实现群聊

![img](https://gitee.com/myzhibei/img/raw/master/427ec38768077732f520457a1f4c577b.png)

4.  Pengsj端点击选择Pengmy后，输入文字后点击Send
    Message后仅在Pengmy端接收到消息，同样Pengmy端可同时发送信息到Pengsj端，实现私聊

![img](https://gitee.com/myzhibei/img/raw/master/64e22b3147d2ec71bf54e6108a7a4098.png)

5.  Pengsj端点击Send File按钮后弹出选择文件框，选择Pengsj.txt文件

![img](https://gitee.com/myzhibei/img/raw/master/ffe7d1e3673d0788fd849c3c1e415aef.png)

6.  点击打开，完成文件上传。

![img](https://gitee.com/myzhibei/img/raw/master/3c1e224040717ec80c3212a4df088ed3.png)

7.  Pengmy端接收到Pengsj发送给你文件的提示并弹出是否接收文件对话框

![img](https://gitee.com/myzhibei/img/raw/master/f174169614f3035c3187d996c9749ba6.png)

8.  点击是后弹出保存文件对话框，选择文件路径及输入保存文件名；

    点击否则显示已拒绝接收该文件

![img](https://gitee.com/myzhibei/img/raw/master/761adf65604cbbb921446e757d764249.png)

9.  点击保存，接收文件成功，实现文件私发

![img](https://gitee.com/myzhibei/img/raw/master/ba9d3da47bee63060bf970f8eba4c4ea.png)

10.  Pengsj端点击all后发送图片文件码农.png

![img](https://gitee.com/myzhibei/img/raw/master/a4f1ef5fb57552f6aa2f5ad0ba885a99.png)

![img](https://gitee.com/myzhibei/img/raw/master/98ed3f73a07ea6eea156a7eafc2b11e1.png)

11.  其它所有客户端均接收到该文件，实现文件群发。

![img](https://gitee.com/myzhibei/img/raw/master/d52ca1b85f5ae8a3de003c87b4978790.png)

![img](https://gitee.com/myzhibei/img/raw/master/6ff05e54b5c59adaff2a723f53f33c4c.png)

![img](https://gitee.com/myzhibei/img/raw/master/6e5fe74b090eefc8243ff0d9a412b1b2.png)

12.  客户端Taow关闭客户端，其它客户端收到Taow退出提示，更新用户列表

![img](https://gitee.com/myzhibei/img/raw/master/2d77af16670098b6c0a8629edff2d9df.png)

13.  客户Pengmy发出关闭服务器指令，实现用户自主关闭服务器

![img](https://gitee.com/myzhibei/img/raw/master/4ece62b1ea6898c2e9a4a2067517098d.png)


关闭服务器及所有客户端，到此各功能测试结束。

 

# 四、   课程设计总结

## (一)、实现的系统功能

实现了用户客户端GUI，用户私发、群发文字信息、用户私发、群发文件、用户自主关闭服务器。

## (二)、使用的Java编程技术

1．使用了多线程技术创建用户发送线程、用户接收线程、服务器对应每个用户的服务器线程；

2．使用CopyOnWriteArrayList，读取操作没有任何同步控制和锁操作，内部数组 array 不会发生修改，只会被另外一个 array 替换，因此可以保证数据安全；写入操作 add() 方法在添加集合的时候加了锁，保证同步，避免多线程写的时候会 copy 出多个副本。

3．使用Socket进行通信，TCP/IP模式，实现网络通信，不再是本地通信；

4．使用Swing创建用户GUI界面，增强美观性、易用性；

5．使用JOptionPane.showOptionDialog等弹出对话框；

6．使用JFileChooser实现文件选择，使用功能性大大增强；

7．使用文件输入输出流实现文件上传下载；

## (三)、遇到的问题及解决

1．服务端用什么储存Socket，后来看了很多视频发现用CopyOnWriteArrayList很适合此场景，ArrayList有很多不同的子类，根据具体情况要具体分析采取合适的类；

2．用什么标识每个客户端，开始想到的是直接用数组的下标，后来发现由于客户端数目一直在变，实现起来容易混乱，后来想到用用户名称来进行标识，简单方便，不容易出错，让客户端一开始就将用户名称发到服务器，实现动态用户列表变化；

3．Swing界面如何布局，用方位布局后总是混乱，后来直接采用了绝对布局进行固定，避免组件位置混乱；

4．发送文件时不能完整传完，会阻塞，采用了分段传输，以每次1KB进行循环传输

5．用户拒绝接收文件后，后续文字、文件传输都无法正常进行，采用假接收不保存进行清空解决；

6．服务器在有客户端关闭的时候不能更新用户列表，采用接收异常时认定该客户端已关闭向其它所有客户端发送该客户端关闭信息；

7．不知道如何去选择文件，发现了JFileChooser类可以非常方便的进行文件选择和保存；                      

8．如何区别是私聊还是群聊，采用了发送信息时自动在消息前面加“@”和“：”，中间夹用户名进行区分，没有@时默认群聊；

9．未使用图形用户界面时，@的用户不存在怎么办，采用服务器检测到不存在后向该客户端询问是否群发，得到Y或N的回应后再处理，采用GUI后由用户直接选择已存在的用户便没有了这个问题；

10．服务器无法关闭，由于服务器没有GUI界面，启动后在后台运行，不容易找到该进程进行关闭，一直占用该端口，无法启动新的进程，采用管理员账户admin发送“Close chat room”时关闭服务器以及其他所有客户端；而其他用户则不会收到能用该语句关闭服务器的提示解决该问题；

 
