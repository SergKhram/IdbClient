<h1 id="app requests" style="color:#333;">Interaction requests</h1>
<h2 style="color:#444;">Accessibility Info Request</h2>
<blockquote>
  <p><strong>Accessibility Info Request</strong>(https://fbidb.io/docs/commands#accessibility-info) - Describes Accessibility Information for the entire screen or for point. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    AccessibilityInfoRequest(
        accessibilityInfo = AccessibilityInfoRequestBody.AccessibilityInfoAllRequestBody(
            format = Format.NESTED //Will report data in the newer nested format, rather than the flat one - if Nested(default is Nested)
        ) or AccessibilityInfoRequestBody.AccessibilityInfoPointRequestBody(
            format = Format.NESTED, //Will report data in the newer nested format, rather than the flat one - if Nested(default is Nested)
            x, //The x-coordinate
            y //The y-coordinate
        )
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Focus Request</h2>
<blockquote>
  <p><strong>Focus Request</strong>(https://fbidb.io/docs/commands#focus-a-simulators-window) - Brings the simulator window to front. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    FocusRequest(),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Hid Requests</h2>
<blockquote>
  <p><strong>Hid Requests</strong>(https://fbidb.io/docs/commands#interact) - For simulators we provide a handful of commands for emulating HID events.</p>

<p><strong>Tap</strong>(https://fbidb.io/docs/commands#tap) - Taps a location on the screen specified in the points coordinate system. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    HidRequest(
        hid = HidRequestBody.TapCmdRequestBody(
            x, //The x-coordinate
            y, //The y-coordinate
            duration //Press duration
        )
    ),
    udid
)</code></pre></p>

<p><strong>Swipe</strong>(https://fbidb.io/docs/commands#swipe) - Swipe from one point to another point. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    HidRequest(
        hid = HidRequestBody.SwipeCmdRequestBody(
            startX, //The x-coordinate of the swipe start point
            startY, //The y-coordinate of the swipe start point
            endX, //The x-coordinate of the swipe end point
            endY, //The y-coordinate of the swipe end point
            deltaValue, //delta in pixels between every touch point on the line between start and end points(Default is 0.0)
            durationValue //Swipe duration(Default is 0.0)
        )
    ),
    udid
)</code></pre></p>

<p><strong>Button press</strong>(https://fbidb.io/docs/commands#press-a-button) - A single press of a button. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    HidRequest(
        hid = HidRequestBody.ButtonPressCmdRequestBody(
            button, //The button name from AppleButton(APPLE_PAY, HOME, LOCK, SIDE_BUTTON, SIRI)
            duration, //Press duration(Default is 0.0)
        )
    ),
    udid
)</code></pre></p>

<p><strong>Text input</strong>(https://fbidb.io/docs/commands#inputting-text) - Input text. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    HidRequest(
        hid = HidRequestBody.TextCmdHidRequestBody(
            text, //Text to input
        )
    ),
    udid
)</code></pre></p>

<p><strong>Key press</strong> - A short press of a keycode or sequence of keycodes. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    HidRequest(
        hid = HidRequestBody.KeyPressCmdRequestBody(
            code, //The key code
            duration, //Press duration
        ) or HidRequestBody.KeysPressCmdRequestBody(
            keys //list of key codes
        )
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Open Url Request</h2>
<blockquote>
  <p><strong>Open Url Request</strong>(https://fbidb.io/docs/commands/#open-a-url) - Opens the specified URL on the target. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    OpenUrlRequest(
        url = "https://facebook.com"
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Send Notification Request</h2>
<blockquote>
  <p><strong>Send Notification Request</strong> - Sent notification. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    SendNotificationRequest(
        bundleId, //Bundle id of the app
        jsonPayload //Notification data in json format
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Set Location Request</h2>
<blockquote>
  <p><strong>Set Location Request</strong>(https://fbidb.io/docs/commands#set-a-simulators-location) - Overrides a simulators' location to the latitude, longitude specified. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    SetLocationRequest(
        latitude, //Latitude to set
        longitude //Longitude to set
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Simulate Memory Warning Request</h2>
<blockquote>
  <p><strong>Simulate Memory Warning Request</strong>- Simulate a memory warning. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    SimulateMemoryWarningRequest(),
    udid
)</code></pre></p>
</blockquote>