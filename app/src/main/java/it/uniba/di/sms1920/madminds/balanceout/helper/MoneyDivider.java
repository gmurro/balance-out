package it.uniba.di.sms1920.madminds.balanceout.helper;

import android.util.Log;

import java.math.BigDecimal;
import java.util.ArrayList;

import it.uniba.di.sms1920.madminds.balanceout.model.Payer;

public class MoneyDivider {

    //metodo che divide l'ammontare di un pagamento tra i debitori, e se l'importo è dispari toglie il centesimo a chi ha pagato la spesa (se è tra i debitori)
    public static ArrayList<Payer> equalDivision(ArrayList<Payer> debitors, double amountPayment, String uidUserLoggedPayer) {

        double singleAmount = amountPayment / debitors.size();
        Log.w("test","singleAmount: "+singleAmount);

        String decimalPart = decimalPart(singleAmount);

        //se la divisione viene con più di due cifre decimali
        if (decimalPart.length() > 2) {

            Log.w("test","# decimal part: "+decimalPart.length());
            int secondDecimalDigit = Integer.parseInt(String.valueOf(decimalPart.charAt(1)));
            int firstDecimalDigit = Integer.parseInt(String.valueOf(decimalPart.charAt(0)));
            int integerPart = (int) singleAmount;

            BigDecimal sumDebts = BigDecimal.ZERO;
            //viene calcolato in questo modo per evitare gli errori del calcolatore
            BigDecimal singleDebt = new BigDecimal(integerPart+"."+firstDecimalDigit+secondDecimalDigit);
            for (Payer p : debitors) {
                //vengono impostati tutti i debiti al valore minore
                p.setAmount(String.valueOf(singleDebt));
                sumDebts = sumDebts.add(singleDebt);
            }

            Log.w("test","sum debt: "+sumDebts);

            String amountPaymentString = String.format("%.2f",amountPayment).replace(",",".");

            //viene aumentato il debito di 1 cent ai debitori finche non si raggiunge la somma pagata
            while (!sumDebts.toString().equals(amountPaymentString)) {
                Log.w("test","if : "+sumDebts.toString()+" == "+ String.valueOf(amountPayment));
                Log.w("test","sum debt: "+sumDebts);
                sumDebts = BigDecimal.ZERO;
                boolean oneIncrease = false;
                for (Payer p : debitors) {

                    //se il debitore è chi ha pagato la somma, non viene aumentato il debito
                    if (p.getIdUser().equals(String.valueOf(uidUserLoggedPayer))) {
                        Log.w("test","payer debt: "+singleDebt);
                        sumDebts = sumDebts.add(singleDebt);
                    } else
                        //viene aumentato il debito
                        if (p.getAmount().equals(String.valueOf(singleDebt)) && !oneIncrease) {
                            BigDecimal amount = new BigDecimal(p.getAmount());
                            amount = amount.add(new BigDecimal("0.01"));
                            Log.w("test","debitor aumented debt: "+amount);
                            p.setAmount(String.format("%.2f",amount).replace(",","."));
                            sumDebts = sumDebts.add(amount);
                            oneIncrease = true;

                        } else {
                            //il debito è già stato aumentato precedentemente
                            Log.w("test","debitor already aumented debt: "+p.getAmount());
                            sumDebts = sumDebts.add(new BigDecimal(p.getAmount()));
                        }
                }

            }

        } else {
            Log.w("test","# decimal part 2 digits");
            for (Payer p : debitors) {
                //vengono impostati tutti i debiti uguali a tutti
                p.setAmount(String.format("%.2f", singleAmount).replace(",","."));
            }
        }

        return debitors;
    }

    //restituisce la parte decimale di un numero con la virgola
    private static String decimalPart(double decimal) {
        String decimalString = String.valueOf(decimal);
        String decimalPart = decimalString.substring(decimalString.lastIndexOf(".") + 1);
        if (decimalPart.equals("0")) {
            return "";
        }
        return decimalPart;
    }


}
