# Send Pull Request Notifications to HipChat Room

1. Provision an API Token for HipChat
   - Login to the Hipchat website
   - Click the `Edit Profile` button in the top-right
   - Click `API Access` in the navigation menu on the left
   - Enter some descriptive text for the `Label`, select `Send Notification` from the `Scopes` combo box, and click the `Create` button
   - Copy the token from the grid; this is needed to authenticate the request
     > **Note:** This value allows API requests to be authenticated as your HipChat user for the selected actions.  For that reason, this token should be kept private!  It is recommended to create individual tokens for every application that uses the HipChat API, to minimize risk if the token is compromised.

1. Get the `API ID` for the HipChat room that will receive notifications
   - Login to the Hipchat website
   - Click on `Rooms` in the navigation bar below the welcome banner
   - Under the `Active` tab, click the name of the room that will receive notifications
   - Copy the `API ID` from the grid, this will be included in the notification request

1. Configure Bitbucket to send notifications to a HipChat room
   - In Bitbucket, go to the repository that will trigger HipChat notifications
   - Click `Settings` (a gear icon, if the sidebar is not expanded)
   - Click `Pull request notifications` under the `ADD-ONS` heading in the navigation menu on the left.
   - Scroll down to `Notifications`.
   - Configure triggers as desired
   - Configure the URL and Headers sections to communicate with HipChat:
     - **URL**: `https://api.hipchat.com/v2/room/{roomId}/notification`
       > Use the `API ID` of the room in place of `{roomId}` in the url above.

     - **Post content**:
       ```json
       {
         "from": "Bitbucket Pull Request",
         "color": "green",
         "message_format": "text",
         "message": "${PULL_REQUEST_DESCRIPTION}",
         "card": {
           "id": "${PULL_REQUEST_FROM_HASH}",
           "style": "link",
           "description": {
             "value": "${PULL_REQUEST_DESCRIPTION}",
             "format": "text"
           },
           "format": "compact",
           "notify": false,
           "url": "${PULL_REQUEST_URL}",
           "title": "[${PULL_REQUEST_FROM_REPO_NAME}] ${PULL_REQUEST_TITLE} (#${PULL_REQUEST_ID})"
         }
        }
       ```
     - **Post content encoding**
       Check the *HTML encode* checkbox.
     - **Headers**:
       
       | Name            | Value               |
       | :-------------- | :------------------ |
       | `Content-Type`  | `application/json`  |
       | `Authorization` | `Bearer {apiToken}` |
       > Use the `API Token` in place of `{apiToken}` in the above value.

1. Trigger your notification by performing one of the actions you configured earlier
   ![](https://raw.githubusercontent.com/tomasbjerre/pull-request-notifier-for-bitbucket/master/sandbox/hipchat.png)

For further customization of the HipChat notification, refer to the [official documentation](https://www.hipchat.com/docs/apiv2/method/send_room_notification).
