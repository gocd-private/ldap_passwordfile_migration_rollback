# ldap_passwordfile_migration_rollback
tool to rollback the migrations from inbuilt ldap and passwordFile to authConfig using plugin

# Build:

```
./gradlew  clean fatJar
```

# Usage: 


* Download and place the jar in a folder say `rollback`.
* Copy `cruise-config.xml` from go-server config folder - Usually '/etc/go/` or `C:\program files\Go Server\config\` (your current config file using authConfig to represent passwordFile and ldap configs) to `rollback` folder
* Copy `go-config-before-migration-91.xml` from go-server config folder to `rollback`
* Copy `go-config-before-migration-92.xml` from go-server config folder to `rollback`

Run the following command:

```
  java -jar ldap_passwordfile_migration_rollback.jar [options]
  Options:
    -configDir
      Folder that contains cruise-config.xml, go-config-before-migration-91.xml, go-config-before-migration-92.xml 
      Default: ./
    -help
      Print this help
```      

Upon a successful rollback, a new file `rescued-cruise-config.xml` would be created in the `-configDir` folder. This is the file you need to set up as your config.
* Copy the file to machine running Go-server
* Stop Go-server
* Copy `rescued-cruise-config.xml` to /etc/go/
* cd `etc/go/`
* Rename cruise-config.xml to cruise-config.xml.bak
* Rename `rescued-cruise-config.xml` to `cruise-config.xml`
* Add a JVM arg to Go-server start up script: `-Dgo.security.inbuilt.auth.enabled=Y`. For linux: edit /etc/default/go-server, For Windows: edit C:\program files (x86)\Go Server\config\wrapper-properties.conf
* Start the server.
* Check logs to make sure there are no errors
* Once the server is up, try logging in. If that works - cool. 
* Now check http://<go-server:8153>/go/admin/config/server to ensure that you see the LDAP and PasswordFile settings in that page.
* Once you have done this (if possible before rolling back) plesse  write to [go-cd mailing list](https://groups.google.com/forum/#!forum/go-cd) or on the [Gitter channel](https://gitter.im/gocd/gocd) to report the issue with a description of why did you have to rollback as that would help us make the fix in the application. 
