<h1 id="app requests" style="color:#333;">App requests</h1>
<h2 style="color:#444;">Launch request</h2>
<blockquote>
  <p><strong>Launch request</strong>(https://fbidb.io/docs/commands#launch-an-app) - Launch an application. Any environment variables of the form IDB_X will be passed through with the IDB_ prefix removed. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    LaunchRequest(
        launch = LaunchRequestBody.StartLaunchRequestBody(
            bundleId, //Bundle id of the app to launch
            env, //env map
            appArgs, //Arguments to start the app with
            foregroundIfRunning, //If the app is already running foreground that process
            waitFor, //Wait for the process to exit, tailing all output from the app
            waitForDebugger, //Suspend application right after the launch to facilitate attaching of a debugger (ex, lldb).
        ),
        predicate = {false}, //Executing until this become true or timeout
        timeout = Duration.ofSeconds(10) //Executing until predicate become true or timeout
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">List apps request</h2>
<blockquote>
  <p><strong>List apps request</strong>(https://fbidb.io/docs/commands#list-apps) - List the installed apps. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    ListAppsRequest(
        suppressProcessState //Fetches App Process State
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Terminate request</h2>
<blockquote>
  <p><strong>Terminate request</strong>(https://fbidb.io/docs/commands#kill-a-running-app) - Terminate a running application. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    TerminateRequest(
        bundleId //Bundle id of the app to terminate
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Uninstall request</h2>
<blockquote>
  <p><strong>Uninstall request</strong>(https://fbidb.io/docs/commands#uninstalling-an-app) - Uninstall an application. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    UninstallRequest(
        bundleId //Bundle id of the app to uninstall
    ),
    udid
)</code></pre></p>
</blockquote>