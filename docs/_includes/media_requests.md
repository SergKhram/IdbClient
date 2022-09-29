<h1 id="app requests" style="color:#333;">Media requests</h1>
<h2 style="color:#444;">Add Media Request</h2>
<blockquote>
  <p><strong>Add Media Request</strong>(https://fbidb.io/docs/commands/#add-media)- Files supplied to this command will be placed in the target's camera roll. Most common image and video file formats are supported. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    AddMediaRequest(
        filePaths //Paths to all media files to add
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Record Request</h2>
<blockquote>
  <p><strong>Record Request</strong>(https://fbidb.io/docs/commands/#record-a-video)- Record the target's screen to a mp4 video file and return byte array. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    RecordRequest(
        predicate = {false}, //Recording until this become true or timeout
        timeout = Duration.ofSeconds(10), //Recording until predicate become true or timeout
    ),
    udid
)</code></pre></p>
<p>You can use the "exportFile" function of the response to prepare the final video file.</p>
</blockquote>

<h2 style="color:#444;">Screenshot Request</h2>
<blockquote>
  <p><strong>Screenshot Request</strong>- Make a screenshot. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    ScreenshotRequest(),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Video Stream Request</h2>
<blockquote>
  <p><strong>Video Stream Request</strong>(https://fbidb.io/docs/video#streaming)- Stream raw H264 from the target. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    VideoStreamRequest(
        fps, //The frame rate of the stream. Default is a dynamic fps
        videoFormat, //The format of the stream
        compressionQuality, //The compression quality (between 0 and 1.0) for the stream
        scaleFactor, //The scale factor for the source video (between 0 and 1.0) for the stream
        predicate = {false}, //Streaming until this become true or timeout
        timeout = Duration.ofSeconds(10), //Streaming until predicate become true or timeout
    ),
    udid
)</code></pre></p>
</blockquote>