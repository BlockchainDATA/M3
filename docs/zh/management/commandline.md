# 命令行工具

cli模块是操作web容器中DHT节点等的工具集。

当M3项目编译后，cli的可执行等jar文件会生成在下列目录

```
cli/build/libs
```

使用 '-h' 选项来获取帮助.

## 命令

    host           Change Current Host Address.
    ncreate        Create Kad Node on current host.
    nconnect       Connect node to local parent node.
    nconnectr      Connect node to remote parent node.
    ninfo          Get node info.
    nlist          List nodes on this host.
    nrefresh       Refresh node info.
    nroute         Get node's route info.
    nsave          Save node's state.
    nshutdown      Shutdown node.
    nshutall       Shutdown All node.
    store          Store Content.
    get            Get Content.
    help           Show Help Content.

## 例子

    ncreate -k 12345678901234567890 -n hello -p 12323
    ncreate -k 12345678901234567891 -n hello1 -p 12324
    store -n hello -c Hello
    get -n hello -o hello -k 8CB644C8354290E6CE597B095AD587043C6B330F
    nconnect -n hello1 -p hello
    nlist
    ninfo hello
    nrefresh hello
    nsave hello
    nroute hello