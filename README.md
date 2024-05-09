# webdav-server
This project is used to build webdavServer

> This project references [webdav-nio-adapter](https://github.com/cryptomator/webdav-nio-adapter)

Like the two pictures here, I mapped the `E:\temp\server` path to the `localhost:9735/webdav` interface. And you can mount this interface by mapping a network drive.

- You can go to `config.properties` under the resources package to configure ports, mapping paths, etc. 
- `realm.properties` configures the account password.
- After you have configured it, you can try running it in the test.
![image](https://github.com/Heachy/webdav-server/assets/90972647/1964cc9e-01e5-46a8-a26d-ffc708125537)

![image](https://github.com/Heachy/webdav-server/assets/90972647/cc60799a-207c-412b-9ed5-c3430280c73e)
