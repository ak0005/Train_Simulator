package sample;

import javafx.scene.chart.NumberAxis;
import javafx.util.StringConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class CustLineChart extends javafx.scene.chart.LineChart {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    //private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    // private Task<Date> task;
    // private int leastTime = 1;
    // ExecutorService executor;

    CustLineChart(NumberAxis xAxis, NumberAxis yAxis, int leastTime) {
        super(xAxis, yAxis);
        //this.leastTime = leastTime;

        xAxis.setLabel("Time/s");
        yAxis.setLabel("Values");
        this.setTitle("Train Chart of " + LocalDateTime.now().format(formatter));

        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                int seconds = object.intValue();
                int p1 = seconds % 60;
                int p2 = seconds / 60;
                int p3 = p2 % 60;
                p2 = p2 / 60;
                return (p2 + ":" + p3 + ":" + p1);
            }

            @Override
            public Number fromString(String string) {
                String arr[] = string.split(":", -2);
                int p1 = Integer.parseInt(arr[0]);
                int p2 = Integer.parseInt((arr[1]));
                int p3 = Integer.parseInt(arr[2]);
                return (p1 * 60 * 60 + p2 * 60 + p3);
            }
        });

        xAxis.setForceZeroInRange(false);
        xAxis.setAutoRanging(true);
        this.setAnimated(false);

        /*task = new Task<Date>() {
            @Override
            protected Date call() throws Exception {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException iex) {
                        Thread.currentThread().interrupt();
                    }
                    if (isCancelled()) {
                        break;
                    }

                    updateValue(new Date());
                }
                return new Date();
            }
        };

        task.valueProperty().addListener(new ChangeListener<Date>() {
            @Override
            public void changed(ObservableValue<? extends Date> observableValue, Date oldDate, Date newDate) {
                String strDate = dateFormat.format(newDate);

            }
        });

        executor = Executors.newSingleThreadExecutor();
        executor.submit(task);*/

        this.setMaxWidth(maxWidth(this.getHeight()));

        Main.stage.setOnCloseRequest(windowEvent -> {
            cancel();
        });
    }

    void cancel() {
        //task.cancel();
        // executor.shutdownNow();
    }
}
