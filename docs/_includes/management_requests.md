<h1 id="app requests" style="color:#333;">Management requests</h1>
<h2 style="color:#444;">Describe Request</h2>
<blockquote>
  <p><strong>Describe Request</strong>(https://fbidb.io/docs/commands/#describe-a-target)- Returns metadata about the specified target. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    DescribeRequest(
        requestBody = TargetDescriptionRequestBody(
            fetchDiagnostics = true //Fetch additional target diagnostics
        )
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Log Request</h2>
<blockquote>
  <p><strong>Log Request</strong>(https://fbidb.io/docs/commands/#log)- Obtain logs from the target or the companion. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    LogRequest(
        predicate = {false}, //Executing until this become true or timeout
        timeout = Duration.ofSeconds(10), //Executing until predicate become true or timeout
        source = LogSource.TARGET, //TARGET or COMPANION
        arguments //Possible arguments:[system | process (pid|process) | parent (pid|process) ][ level default|info|debug][ predicate &lt;predicate&gt; ][ source ][ style (syslog|json) ][ timeout &lt;num&gt;[m|h|d] ][ type activity|log|trace ]
    ),
    udid
)</code></pre></p>
</blockquote>