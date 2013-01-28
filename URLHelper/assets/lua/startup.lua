local _G = _G

do
    local Toast = luajava.bindClass("android.widget.Toast")
    local PreferenceManager = luajava.bindClass("android.preference.PreferenceManager")
    local SharedPrefs = PreferenceManager:getDefaultSharedPreferences(ctx)
    
    function _G.toast(msg)
      t = Toast:makeText(ctx, msg, Toast.LENGTH_LONG)
      t:show()
    end
    
    _G.prefs = setmetatable({},
                            {
                               __index = function(t, key)
                                   -- Should the default be different?
                                   return SharedPrefs:getString(key, "")
                                end
                            })                                 
end

x = prefs["launchimm"]
toast(x)
