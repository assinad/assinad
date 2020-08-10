package a.sign.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.EditText;

import a.sign.R;

public class UserMsgService {

    private static EditText etRegCellNum;
    private static Activity localActivity;

    // used in order to set title and message with xml int
    public static void showDialog(Context context, int title, int msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(R.string.button_title_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        dialog.dismiss();
                    }
                }).show();

    }

    // used in order to set title and message with string
    // used in order to finish the activity when back button is pressed
    public static void showDialogFinish(final Activity activity, String title,
                                        String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(msg);

        // listener for back button is pressed
        builder.setPositiveButton(R.string.button_title_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        activity.finish();
                    }
                })
                .show().setOnDismissListener(
                new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        activity.finish();
                    }
                });
    }

    // used in order to set title and message with R.String...
    // used in order to finish the activity when back button is pressed
    public static void showDialogFinish(final Activity activity, int title,
                                        int msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(msg);

        // listener for back button is pressed
        builder.setPositiveButton(R.string.button_title_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                activity.finish();
            }
        }).show().setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                activity.finish();
            }
        });
    }

    public static void showDialogPositButtonMail(final Activity activity,
                                                 final Context context, int title, int msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        // .setView(input)

        builder.setPositiveButton(R.string.button_title_send,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        // it's not ACTION_SEND
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT,
                                context.getResources().getText(
                                        R.string.alertdialog_internal_problem_title));
                        intent.putExtra(
                                Intent.EXTRA_TEXT, context.getResources().getText(
                                        R.string.alertdialog_internal_problem_email_msg));
                        // or just "mailto:" for blank
                        intent.setData(Uri
                                .parse("mailto:" + context.getResources().getText(
                                        R.string.email_address)));

                        // your app is displayed, instead of the email app, after sending the email.
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                        activity.finish();
                    }
                });
        builder.setNegativeButton(R.string.button_title_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        activity.finish();
                    }
                }).show();

    }

    public static void showDialogPositButtonMailUserReg(final Activity activity,
                                                        final Context context, int title, int msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        // .setView(input)

        builder.setPositiveButton(R.string.button_title_send,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        // it's not ACTION_SEND
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT,
                                context.getResources().getText(
                                        R.string.alertdialog_user_registration_problem_msg));
                        intent.putExtra(
                                Intent.EXTRA_TEXT, context.getResources().getText(
                                        R.string.alertdialog_user_registration_problem_email_msg));
                        // or just "mailto:" for blank
                        intent.setData(Uri
                                .parse("mailto:" + context.getResources().getText(
                                        R.string.email_address)));

                        // your app is displayed, instead of the email app, after sending the email.
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);

                        activity.finish();
                    }
                });
        builder.setNegativeButton(R.string.button_title_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        activity.finish();
                    }
                }).show();

    }

    public static void updateApp(final Activity activity){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.alertdialog_app_upgrade_title);
        builder.setMessage(R.string.alertdialog_app_upgrade_msg);
        builder.setPositiveButton(R.string.button_title_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String my_package_name = "a.sign";  // <- HERE YOUR PACKAGE NAME!!
                String url = "";

                try {
                    //Check whether Google Play store is installed or not:
                    activity.getPackageManager().getPackageInfo("com.android.vending", 0);

                    url = "market://details?id=" + my_package_name;
                } catch (final Exception e) {
                    url = "https://play.google.com/store/apps/details?id=" + my_package_name;
                }

                //Open the app page in Google Play store:
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                activity.startActivity(intent);
            }
        }).show();
    }

    public static void sendEmail(final Activity activity,
                                 final Context context, String title, String msg, String email) {

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(
                Intent.EXTRA_TEXT, msg);
        // or just "mailto:" for blank
        intent.setData(Uri
                .parse("mailto:" + email));


        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        // your app is displayed, instead of the email app, after sending the email.
        activity.finish();


    }
}
