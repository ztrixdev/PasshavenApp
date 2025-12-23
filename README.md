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
    <td><img src="https://raw.githubusercontent.com/ztrixdev/PasshavenApp/refs/heads/main/readme/login_scrshot.png" alt="Login" width="250"></td>
    <td><img src="https://raw.githubusercontent.com/ztrixdev/PasshavenApp/refs/heads/main/readme/scrsht_view_entry.png" alt="View" width="250"></td>
  </tr>
</table>

## Current status: Infdev
The project is in it's soopa (as in [soopa high level tactics, remember that yeah?](https://www.youtube.com/watch?v=Wop7Ld0eX1g)) early stage of development.   
Here's a quick list of what's done and what's TODO:

- Done:  
    1. The encryption mechanism
    2. The database is fully set up, ready to serve
    3. The export system (though so far it's only exporting in sort of a proprietary format)
    4. Folder and entry creation
    5. Theming (incl. Dynamic Colors)
    6. Entry management
    7. Settings menu
- TODO:
  1. Vault overview page
  2. 2FA overview page
  3. Importing (to recover a Ph vault or to migrate from BW/VW)

There's still a lot to be done, but I expect the project to be available for beta testing by Jan/Feb 2026.
