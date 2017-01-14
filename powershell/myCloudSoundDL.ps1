$baseURL = "https://api.soundcloud.com"
$clientID = "cc96dfbae2768d9291e729d107f89792"
$endURL = "client_id=$clientID"
$resolveURL = "$baseURL/resolve.json?url="
$SC = @{
"connect"=@{}
"oauth2"=@{"token"=@{}}
"users"=@{}
"tracks"=@{}
"playlists"=@{}
"comments"=@{}
"me"=@{"connections"=@{};"activities"=@{}}
"apps"=@{}
"resolve"=@{}
"oembed"=@{}
}

<#

Function Get-SoundCloudUser
{
    param([int]$userID,[string]$userURL)
    if ($userID)
    {
        $apiURL = "$baseURL/users/${userID}?$endURL"
        $userHTML = Invoke-WebRequest -Uri $apiURL -Method Get
        $user = $userHTML.Content | ConvertFrom-Json
    }
    elseif ($userURL)
    {
        $userIDURL = "$resolveURL$userURL&$endURL"
        $userHTML = Invoke-WebRequest -Uri $userIDURL -Method Get
        $user = $userHTML.Content | ConvertFrom-Json | Select -Property *
    }
    else
    {
        Throw "Rekt. Need a value for -userID parameter or -trackURL parameter."
    }
    $user
}

Function Get-SoundCloudTrack
{
    param([int]$trackID,[string]$trackURL)
    if ($trackID)
    {
        $apiURL = "$baseURL/tracks/${trackID}?$endURL"
        $trackHTML = Invoke-WebRequest -Uri $apiURL -Method Get
        $track = $trackHTML.Content | ConvertFrom-Json
    }
    elseif ($trackURL)
    {
        $trackIDURL = "$resolveURL$trackURL&$endURL"
        $trackHTML = Invoke-WebRequest -Uri $trackIDURL -Method Get
        $track = $trackHTML.Content | ConvertFrom-Json | Select -Property *
    }
    else
    {
        Throw "Rekt. Need a value for -trackID parameter or -trackURL parameter."
    }
    $track
}



<# get user example #>
#Get-SoundCloudUser -userID 20892733
#Get-SoundCloudUser -userURL "https://soundcloud.com/eric-calvano"

<# get track example #>
#Get-SoundCloudTrack -trackURL https://soundcloud.com/destroyed-by-seek-n-destroy

#>
