<h1 id="app requests" style="color:#333;">Crash requests</h1>
<h2 style="color:#444;">Crash Delete Request</h2>
<blockquote>
  <p><strong>Crash Delete Request</strong>(https://fbidb.io/docs/commands#delete-crash-logs) - Delete a crash log. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    CrashDeleteRequest(
        requestBody = CrashLogQueryRequestBody(
            name //The unique name of the crash
        )
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Crash List Request</h2>
<blockquote>
  <p><strong>Crash List Request</strong>(https://fbidb.io/docs/commands#list-crash-logs) - List the available crashes. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    CrashListRequest(
        requestBody = CrashLogQueryRequestBody(
            since, //Match based on being newer than the provided unix timestamp in long
            before, //Match based older than the provided unix timestamp in ling
            bundleId //Filter based on the bundle id of the crashed process
        )
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Crash Show Request</h2>
<blockquote>
  <p><strong>Crash Show Request</strong>(https://fbidb.io/docs/commands#fetch-a-crash-log) - Fetches the crash log with the specified name. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    CrashDeleteRequest(
        requestBody = CrashLogQueryRequestBody(
            name //The unique name of the crash
        )
    ),
    udid
)</code></pre></p>
</blockquote>