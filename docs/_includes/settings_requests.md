<h1 id="app requests" style="color:#333;">Settings requests</h1>
<h2 style="color:#444;">Approve Request</h2>
<blockquote>
  <p><strong>Approve Request</strong>(https://fbidb.io/docs/commands/#approve)- For simulators idb can programmatically approve permission for an app. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    ApproveRequest(
        bundleId, //Bundle id of the app
        permissions //list of permissions from the list - PHOTOS,CAMERA,CONTACTS,URL,LOCATION,NOTIFICATION,MICROPHONE
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Clear Keychain Request</h2>
<blockquote>
  <p><strong>Clear Keychain Request</strong>(https://fbidb.io/docs/commands/#clear-the-keychain)- For simulators idb can clear the entire keychain. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    ClearKeychainRequest(),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Contacts Update Request</h2>
<blockquote>
  <p><strong>Contacts Update Request</strong>(https://fbidb.io/docs/commands/#add-contacts)- For simulators idb can overwrite the simulators contacts db. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    ContactsUpdateRequest(
        contactsDbFile // sqlite file's path with contacts
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Get Setting Request</h2>
<blockquote>
  <p><strong>Get Setting Request</strong>- Gets a preference. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    GetSettingRequest(
        setting = GetSettingRequestBody.LocaleSetting() //Gets a local preference value
            or GetSettingRequestBody.AnySetting(
                name, //Preference name
                domain //Preference domain, assumed to be Apple Global Domain if not specified
            )
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Get Settings Request</h2>
<blockquote>
  <p><strong>Get Settings Request</strong>- Settings' list by type. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    GetSettingsRequest(
        settingType //settings' type - LOCALE(default) or ANY
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Revoke Request</h2>
<blockquote>
  <p><strong>Revoke Request</strong>- Revoke permissions for an app. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    RevokeRequest(
        bundleId, //Bundle id of the app
        permissions, //Permissions to revoke from the list - PHOTOS,CAMERA,CONTACTS,URL,LOCATION,NOTIFICATION,MICROPHONE
        scheme //Url scheme registered by the app to revoke
    ),
    udid
)</code></pre></p>
</blockquote>

<h2 style="color:#444;">Setting Request</h2>
<blockquote>
  <p><strong>Setting Request</strong>- Sets a preference. Example: 
<pre class="highlight"><code class="language-plaintext highlighter-rouge">idb.execute(
    SettingRequest(
        setting = SettingRequestBody.HardwareKeyboardSetting(
            enabled //activate/deactivate HardwareKeyboard setting
        ) or SettingRequestBody.LocaleSetting(
            localeIdentifier //Preference value
        ) or SettingRequestBody.AnySetting(
            name, //Preference name
            value, //Preference value
            valueType, //Specifies the type of the value to be set, for supported types see 'defaults get help' defaults to string. Example of usage: idb set --domain com.apple.suggestions.plist SuggestionsAppLibraryEnabled --type bool true
            domain //Preference domain, assumed to be Apple Global Domain if not specified
        )
    ),
    udid
)</code></pre></p>
</blockquote>