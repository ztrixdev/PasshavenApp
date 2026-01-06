# Passhaven
## The ultimate local-only secrets manager

> _<it>Passwords are too sensitive to store on a remote cloud.
>  Data leaks happen, politics change and you never know which other service will be unavailable in your region tomorrow... 
>  Sure, you can self-host a password manager like Vaultwarden, but that requires either a rented VPS or a 24/7 running home server. 
>  In addition to that, maintaining the stability of such a solution... Ain't nobody got time for that, am I right?
> </it>_

  Passhaven, on the other hand, offers you a free (as in freedom) approach to managing your secrets. The vault, just like in [Aegis](https://github.com/beemdevelopment/Aegis) (from which I took inspiration to create Ph),
  is stored fully encrypted and protected only on your phone. An Internet connection is not required to log into your vault and see your passwords,
  a cloud server is not needed. Both the encryption and storage happen on your phone. Using our automatic password-protected backup system, while also following the [3-2-1 backup rule](https://www.veeam.com/blog/321-backup-rule.html), 
  you can build an unbreachable digital fortress that will store your sensitive information just right, unless, of course, you share your Passhaven PIN and MP with everyone on your Discord server. In that case.. there is probably no hope for your personal data >:3

## Screenshots

<table>
  <tr>
    <td><img src="https://raw.githubusercontent.com/ztrixdev/PasshavenApp/refs/heads/main/readme/scrshot_intro_act.png" alt="Intro" width="250"></td>
    <td><img src="https://raw.githubusercontent.com/ztrixdev/PasshavenApp/refs/heads/main/readme/vault_overview_early_dev_scrshot.jpg" alt="Overview" width="250"></td>
    <td><img src="https://raw.githubusercontent.com/ztrixdev/PasshavenApp/refs/heads/main/readme/new_entry_early_dev_scrshot.jpg" alt="New entry" width="250"></td>
    <td><img src="https://raw.githubusercontent.com/ztrixdev/PasshavenApp/refs/heads/main/readme/new_folder_scrshot.jpg" alt="New folder" width="250"></td>
    
  </tr>
</table>

## Current status: Alpha
The project is facing a major UI/UX overhaul, as well as a full 2FA rework (moving from an obsolete Google Auth library that is neither customizable nor stable to a <a href="https://codeberg.org/ztrixdev/j2fa" target="_blank">self-written one</a>)  
Most of the app's backend functionality is done and just needs a UI wrapper to serve a purpose.

