# Command Line Tools

cli is a command line toolset to manage the DHT nodes in the web server.

When the project is built, the jar files is generated in following directory.

```
cli/build/libs
```

Use '-h' Option to get help.

## Commands

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

## Examples

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