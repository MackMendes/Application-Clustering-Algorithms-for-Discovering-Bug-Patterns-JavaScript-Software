package ca.ubc.ece.salt.pangor.original.analysis;

import ca.ubc.ece.salt.pangor.original.analysis.Commit;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileAnalysis;
import ca.ubc.ece.salt.pangor.original.analysis.SourceCodeFileChange;
import ca.ubc.ece.salt.pangor.original.cfd.CFDContext;
import ca.ubc.ece.salt.pangor.original.cfd.ControlFlowDifferencing;
import ca.ubc.ece.salt.pangor.original.cfg.CFGFactory;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.storage.IRelation;
import org.mozilla.javascript.EvaluatorException;

public class DomainAnalysis {
    protected SourceCodeFileAnalysis srcAnalysis;
    protected SourceCodeFileAnalysis dstAnalysis;
    protected CFGFactory cfgFactory;
    private boolean preProcess;

    public DomainAnalysis(SourceCodeFileAnalysis srcAnalysis, SourceCodeFileAnalysis dstAnalysis, CFGFactory cfgFactory, boolean preProcess) {
        this.srcAnalysis = srcAnalysis;
        this.dstAnalysis = dstAnalysis;
        this.cfgFactory = cfgFactory;
        this.preProcess = preProcess;
    }

    public void analyze(Commit commit, Map<IPredicate, IRelation> facts) throws Exception {
        if (!this.preAnalysis(commit, facts)) {
            return;
        }
        for (SourceCodeFileChange sourceCodeFileChange : commit.sourceCodeFileChanges) {
            this.analyzeFile(sourceCodeFileChange, facts);
        }
        this.postAnalysis(commit, facts);
    }

    protected boolean preAnalysis(Commit commit, Map<IPredicate, IRelation> facts) throws Exception {
        return true;
    }

    protected void postAnalysis(Commit commit, Map<IPredicate, IRelation> facts) throws Exception {
    }

    protected void analyzeFile(SourceCodeFileChange sourceCodeFileChange, Map<IPredicate, IRelation> facts) throws Exception {
        String fileExtension = DomainAnalysis.getSourceCodeFileExtension(sourceCodeFileChange.buggyFile, sourceCodeFileChange.repairedFile);
        if (fileExtension != null && this.cfgFactory.acceptsExtension(fileExtension)) {
            ControlFlowDifferencing cfd = null;
            try {
                String[] arrstring;
                if (this.preProcess) {
                    String[] arrstring2 = new String[3];
                    arrstring2[0] = sourceCodeFileChange.buggyFile;
                    arrstring2[1] = sourceCodeFileChange.repairedFile;
                    arrstring = arrstring2;
                    arrstring2[2] = "-pp";
                } else {
                    String[] arrstring3 = new String[2];
                    arrstring3[0] = sourceCodeFileChange.buggyFile;
                    arrstring = arrstring3;
                    arrstring3[1] = sourceCodeFileChange.repairedFile;
                }
                String[] args = arrstring;
                cfd = new ControlFlowDifferencing(this.cfgFactory, args, sourceCodeFileChange.buggyCode, sourceCodeFileChange.repairedCode);
            }
            catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("ArrayIndexOutOfBoundsException: possibly caused by empty file.");
                return;
            }
            catch (EvaluatorException e) {
                System.err.println("Evaluator exception: " + e.getMessage());
                return;
            }
            catch (Exception e) {
                throw e;
            }
            CFDContext cfdContext = cfd.getContext();
            if (this.srcAnalysis != null) {
                this.srcAnalysis.analyze(sourceCodeFileChange, facts, cfdContext.srcScript, cfdContext.srcCFGs);
            } else {
                this.srcAnalysis.analyze(sourceCodeFileChange, facts, cfdContext.srcScript, cfdContext.srcCFGs);
            }
            if (this.dstAnalysis != null) {
                this.dstAnalysis.analyze(sourceCodeFileChange, facts, cfdContext.dstScript, cfdContext.dstCFGs);
            } else {
                this.dstAnalysis.analyze(sourceCodeFileChange, facts, cfdContext.dstScript, cfdContext.dstCFGs);
            }
        }
    }

    protected static String getSourceCodeFileExtension(String preCommitPath, String postCommitPath) {
		Pattern pattern = Pattern.compile("\\.([a-z]+)$");
		Matcher preMatcher = pattern.matcher(preCommitPath);
		Matcher postMatcher = pattern.matcher(postCommitPath);
		
		String preExtension = null;
		String postExtension = null;
		if ((preMatcher.find()) && (postMatcher.find()))
		{
		  preExtension = preMatcher.group(1);
		  postExtension = postMatcher.group(1);
		  if (preExtension.equals(postExtension)) {
			return preExtension;
		  }
		}
		return null;
    }
}

