<h1 id="app requests" style="color:#333;">File requests</h1>
<h2 style="color:#444;">Ls Request</h2>
<blockquote>
  <p><strong>Ls Request</strong>(https://fbidb.io/docs/file-containers#list-a-path-on-a-target) - Returns a list of all the files present within one or more directories. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    LsRequest(
        lsRequestBody = LsRequestBody.SingleLsRequestBody(
            path, //Source path
            container //File container(Default is ROOT)
        ) or LsRequestBody.MultipleLsRequestBody(
            paths, //List of source paths
            container //File container(Default is ROOT)
        )
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Mkdir Request</h2>
<blockquote>
  <p><strong>Mkdir Request</strong>(https://fbidb.io/docs/file-containers#make-a-new-directory) - Make a directory inside an application's container. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    MkdirRequest(
        path, //Path to directory to create
        container //File container(Default is ROOT)
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Mv Request</h2>
<blockquote>
  <p><strong>Mv Request</strong>(https://fbidb.io/docs/file-containers#moving-files-within-the-container) - Move a path inside an application's container. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    MvRequest(
        srcPaths, //Source paths relative to Container
        dstPath, //Destination path relative to Container
        container //File container(Default is ROOT)
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Pull Request</h2>
<blockquote>
  <p><strong>Pull Request</strong>(https://fbidb.io/docs/file-containers#copying-files-out-of-a-container) - Copy a file inside an application's container. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    PullRequest(
        srcPath, //Relative Container source path
        container //File container(Default is ROOT)
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Push Request</h2>
<blockquote>
  <p><strong>Push Request</strong>(https://fbidb.io/docs/file-containers#copying-files-into-a-container) - Copy file(s) from local machine to target. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    PushRequest(
        srcPath, //Path of file(s) to copy to the target
        dstPath, //Directory relative to the data container of the application to copy the files into. Will be created if non-existent.(just directory)
        container //File container(Default is ROOT)
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Rm Request</h2>
<blockquote>
  <p><strong>Rm Request</strong>(https://fbidb.io/docs/file-containers#remove-a-path-on-a-target) - Remove an item inside a container. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    RmRequest(
        paths, //Path of item to remove (A directory will be recursively deleted)
        container //File container(Default is ROOT)
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Tail Request</h2>
<blockquote>
  <p><strong>Tail Request</strong> - Tails a remote file. String as the result. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    TailRequest(
        path, //Relative container source path
        container, //File container(Default is ROOT)
        predicate = {false}, //Executing until this become true or timeout
        timeout = Duration.ofSeconds(10) //Executing until predicate become true or timeout
    ),
    udid
)</code></pre></p>
</blockquote>