/*
 * Decompiled with CFR 0_123.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package ca.ubc.ece.salt.pangor.original.batch;

import ca.ubc.ece.salt.pangor.original.batch.GitProjectAnalysis;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GitProjectAnalysisTask
implements Callable<Void> {
    protected final Logger logger = LogManager.getLogger(GitProjectAnalysisTask.class);
    private GitProjectAnalysis gitProjectAnalysis;
    private CountDownLatch latch;

    public GitProjectAnalysisTask(GitProjectAnalysis gitProjectAnalysis, CountDownLatch latch) {
        this.gitProjectAnalysis = gitProjectAnalysis;
        this.latch = latch;
    }

    @Override
    public Void call() throws Exception {
        try {
            this.gitProjectAnalysis.analyze();
        }
        catch (Exception e) {
            try {
                System.err.println("[ERR] Exception on GitProjectAnalysisTask");
                e.printStackTrace();
            }
            catch (Throwable throwable) {
                this.logger.info(" [TASK FINALIZED] {} tasks left", new Object[]{this.latch.getCount()});
                this.latch.countDown();
                throw throwable;
            }
            this.logger.info(" [TASK FINALIZED] {} tasks left", new Object[]{this.latch.getCount()});
            this.latch.countDown();
        }
        this.logger.info(" [TASK FINALIZED] {} tasks left", new Object[]{this.latch.getCount()});
        this.latch.countDown();
        return null;
    }
}

