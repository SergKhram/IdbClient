<h1 id="quick-start" style="color:#333;">Quick start</h1>
<h3 style="color:#444;">Add IdbClient dependency</h3>
<div class="language-plaintext highlighter-rouge"><div class="highlight"><pre class="highlight"><code><&#0dependency>
  <&#0groupId>io.github.sergkhram<&#0/groupId>
  <&#0artifactId>idbclient<&#0/artifactId>
  <&#0version>0.0.2-RELEASE<&#0/version>
<&#0/dependency>
</code></pre></div></div>
<h3 style="color:#444;">Create idbClient</h3>
<div class="language-plaintext highlighter-rouge"><div class="highlight"><pre class="highlight"><code>val idb = IOSDebugBridgeClient()
</code></pre></div></div>
<h3 style="color:#444;">Connect companion</h3>
<div class="language-plaintext highlighter-rouge"><div class="highlight"><pre class="highlight"><code>val udid = idb.connectToCompanion(TcpAddress("127.0.0.1", 10882))
</code></pre></div></div>
<h3 style="color:#444;">Execute request</h3>
<div class="language-plaintext highlighter-rouge"><div class="highlight"><pre class="highlight"><code>val result = idb.execute(
    LogRequest(
        predicate = {false}, 
        timeout = Duration.ofSeconds(10)
    ),
    udid
)
result.forEach(::println)
</code></pre></div></div>