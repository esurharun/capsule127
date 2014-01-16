~~~~~
  ____  _____   ______    ______ __ __ |  |    ____  /_   |\_____  \ \______  \
_/ ___\ \__  \  \____ \  /  ___/|  |  \|  |  _/ __ \  |   | /  ____/     /    /
\  \___  / __ \_|  |_> > \___ \ |  |  /|  |__\  ___/  |   |/       \    /    / 
 \___  >(____  /|   __/ /____  >|____/ |____/ \___  > |___|\_______ \  /____/  
     \/      \/ |__|         \/                   \/               \/          
~~~~~

Capsule127 is a password cracker using data-grid technologies. 

Currently available for all platforms with JRE 6 but not tested on Windows platforms. 
It is using Hazelcast as open-source datagrid for high-performance data flow over network
and concurrency necessities. 

It's at very early stage.

For now, it just supports following hash types;

* (MY4) MySql pre 4.1 
* (MY5) Mysql post 4.1 
* (MS2005) Microsoft Sql Server 2005-2008
* (MS2012) Microsoft Sql Server >= 2012
* (O11) Oracle < 11g (DES)
* (O) Oracle >= 11g (SHA1)
* (LM) LanMan Hash
* (NT) NT Hash

Planning to implement;

* WPA-WPA2 Pre Shared Keys
* SHA1, SHA256, SHA512
* ZIP file
* PDF
* SHA3 (Keccak)
* Camellia
* MD4, MD5
* HMAC-MD5
* HMAC-SHA1
* Cisco PIX 
* RipeMD160
* Whirlpool
* Various TrueCrypt 5+ hashes 
* Blowfish (openbsd)

But if you want to implement your own hash algorithm, it is very simple; you should just implement 
IHashTypeDescription and IHashGenerator interfaces and add your class to App.supportedHashTypes 
array.

Hash file format is very simple to understand. It's pattern is just like below;

**[HASHTYPE]**$**[USERNAME]**#**[HASH]**

Hash type values are included in sources which implements IHashTypeDescription classes. Take look at 
the implemented hash types paragraph up, you should see the hash types within parantheses. Here is an
example hash file down below;


~~~~
O$SIMON#4F8BC1809CB2AF77
O11$SYSTEM#71752CE0530476A8B2E0DD218AE59CB71B211D7E1DB70EE23BFB23BDFD48
MS2005$SIMON#0x01006ACDF9FF5D2E211B392EEF1175EFFE13B3A368CE2F94038B
~~~~

How to install
--------------

You should have Maven installed.

Just clone the git repository and run

  `mvn compile exec:java`

If you want to deploy as a jar file just type

  `mvn install`

and just copy the file target/com.capsule127-[version].jar to any platform you want. Jar file includes
all necessary files within. 

How to run
----------

It's very easy.

If you hava jar binary just type 

  `java -jar com.capsule127-[version].jar` 
  
or execute it with maven by typing 

  `mvn clean compile exec:java`
  
After capsule127 starts, you should start a node by typing 

  `c127> ns` 
  
  or
  
  `c127> node-start`
  
after that you can import your hash files by using `hashes-import` command

  `c127> hashes-import src/test/common/sample_hashes.txt`
  
Capsule127 imports all hashes and dictionaries to the cloud. 

And you can import your dictionaries -which can be in .gz format- by using
dict-import command.

  `c127> dict-import my_own_dict.dic.gz`
  
After you imported all of necessary stuff to the cloud, then you can start 
cracking your hashes by using `lc` or `launch-cracker` command with the type
of hash indicator such as O , O11 etc.

  `c127> lc O`

You should see some lines like the one below;

`CRACKER: Running on 160099.0 p/s, hash count = 3179, wordlist chunks = 107, tried pass count= 3784281K, curr pass = tianph `

The good part is that you can start-stop cracker any time at any node on the network
and don't worry about data-loss or various problems such as shutdowns etc. Hazelcast handles
all the failures.

If you have more than one node on the network, your data will be transffered to the last
one if you have proper shutdowns of JVMs.

You can dump hashes left on cloud (not cracked yet) by using `hashes-import` command.

You can see any time whether a node joined on network or disconnected on capsule127 
interface live like below;


~~~~~
Members [5] {
	Member [192.168.0.103]:5701
	Member [192.168.0.106]:5701
	Member [192.168.0.105]:5701
	Member [192.168.0.105]:5702
	Member [192.168.0.10]:5701 this
}
~~~~~

You can add a remote node which is not automatically-discovered by capsule127 by adding
it manually to using `add-node` command.

When a password is cracked, it is broadcasted to all nodes accross network and
saved to capsule127.jackpot file automatically. 

By default capsule127 creates a workgroup named C127 with C127 password. But you can
change your workgroup name and password by using `set` keyword.


For more information and help, just get in touch with developer esur[dot]harun[at]gmail[dot]com

Planning features

* Brute-Force cracking (mostly done)
* Rules file integration for generating passwords to crack
* Adding various hash implementations
* Hash-file conversion tools from JTR and various hash-file types.
* Pause/Resume functions to pause/resume all the cloud
* Native interface (depends on Hazelcast c++ implementation otherwise should change data-grid module)
* Adding Jack The Ripper, oclHashCat integration
* Web interface (maybe?)
