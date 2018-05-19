package ca.ubc.ece.salt.pangor.learn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.factory.Factory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.WekaException;
import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.pangor.original.analysis.Commit.Type;
import ca.ubc.ece.salt.pangor.original.api.KeywordUse;
import ca.ubc.ece.salt.pangor.original.api.KeywordUse.KeywordContext;
import ca.ubc.ece.salt.pangor.original.learn.Cluster;
import ca.ubc.ece.salt.pangor.original.learn.ClusterMetrics;
import ca.ubc.ece.salt.pangor.original.learn.EvaluationResult;
import ca.ubc.ece.salt.pangor.original.learn.analysis.LearningDataSet;
import ca.ubc.ece.salt.pangor.original.learn.analysis.LearningMetrics;
import ca.ubc.ece.salt.pangor.original.learn.analysis.LearningMetrics.KeywordFrequency;

/**
 * Creates clusters similar repairs.
 *
 * 1. Reads the results of the data mining task ({@code LearningAnalysisMain})
 * 2. Builds a CSV file of WEKA-supported feature vectors
 * 3. Clusters the feature vectors
 */
public class LearningDataSetMain {

	protected static final Logger logger = LogManager.getLogger(LearningDataSetMain.class);

	/**
	 * Creates the learning data sets for extracting repair patterns.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		LearningDataSetOptions options = new LearningDataSetOptions();
		CmdLineParser parser = new CmdLineParser(options);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			LearningDataSetMain.printUsage(e.getMessage(), parser);
			return;
		}

		/* Print the help page. */
		if(options.getHelp()) {
			LearningDataSetMain.printHelp(parser);
			return;
		}

		/* Get the clusters for the data set. */

		/* The clusters stored according to their keyword. */
		Set<ca.ubc.ece.salt.pangor.original.learn.ClusterMetrics> keywordClusters = 
				new TreeSet<ca.ubc.ece.salt.pangor.original.learn.ClusterMetrics>(new Comparator<ca.ubc.ece.salt.pangor.original.learn.ClusterMetrics>() {
			@Override
			public int compare(ca.ubc.ece.salt.pangor.original.learn.ClusterMetrics c1, 
					ca.ubc.ece.salt.pangor.original.learn.ClusterMetrics c2) {
				if(c1.totalInstances == c2.totalInstances) return c1.toString().compareTo(c2.toString());
				else if(c1.totalInstances < c2.totalInstances) return 1;
				else return -1;
			}
		});

		/* Re-construct the data set. */
		LearningDataSet clusteringDataSet =
				LearningDataSet.createLearningDataSet(
						options.getDataSetPath(),
						options.getOraclePath(),
						new LinkedList<KeywordUse>(), // column filters
						options.getEpsilon(),
						options.getComplexityWeight(),
						options.getMinClusterSize());

		/* Store the total instances in the dataset before filtering. */
		
		ca.ubc.ece.salt.pangor.original.learn.ClusterMetrics clusterMetrics = 
				new ca.ubc.ece.salt.pangor.original.learn.ClusterMetrics();

		/* Pre-process the file. */
		clusteringDataSet.preProcess(getBasicRowFilterQuery(options.getMaxChangeComplexity()));
		clusterMetrics.setTotalInstances(clusteringDataSet.getSize());
		clusteringDataSet.preProcess(getStatementRowFilterQuery(options.getMaxChangeComplexity()));

		/* Print the metrics from the processed data set. */
		LearningMetrics metrics = clusteringDataSet.getMetrics();
		for(KeywordFrequency frequency : metrics.changedKeywordFrequency) {
			System.out.println(frequency.keyword + " : " + frequency.frequency);
		}

		/* Get the clusters. */
		try {

			weka.core.Instances dataSetWeka = null;
			
			// Abaixo obtendo o cabeçalho do vetor de característica
			String featureVectorHeader = clusteringDataSet.getLearningFeatureVectorHeader();
			
			// Obtendo todos os atributos (cabeçalho) do Header
			//ArrayList<Attribute> header = clusteringDataSet.getWekaAttributes();
			
			// Obtendo o dataset do resultado do Weka
			dataSetWeka = clusteringDataSet.getWekaDataSet();

			//weka.core.Instances freeStructures = dataSetWeka.stringFreeStructure();
			
			// Instância de String Builder
			StringBuilder sb = new StringBuilder();
			
			//sb.append("ID, ProjectID, BugFixingCommit, CommitURL, BuggyCommitID, RepairedCommitID, Class, Method, Cluster, ModifiedStatementCount, METHOD:METHOD_CALL:INSERTED:global:reduceRight, METHOD:METHOD_CALL:INSERTED:global:min, METHOD:ARGUMENT:INSERTED:global:min, METHOD:METHOD_CALL:REMOVED:global:min, METHOD:METHOD_CALL:UPDATED:global:min, METHOD:METHOD_CALL:INSERTED:global:pop, METHOD:METHOD_CALL:REMOVED:global:pop, METHOD:METHOD_CALL:UPDATED:global:pop, RESERVED:ASSIGNMENT_LHS:REMOVED:global:class, RESERVED:ASSIGNMENT_LHS:INSERTED:global:class, RESERVED:ARGUMENT:INSERTED:global:class, RESERVED:ASSIGNMENT_RHS:REMOVED:global:class, METHOD:METHOD_CALL:INSERTED:global:pow, METHOD:METHOD_CALL:REMOVED:global:pow, METHOD:METHOD_CALL:INSERTED:global:call, METHOD:METHOD_CALL:REMOVED:global:call, METHOD:METHOD_CALL:UPDATED:global:call, METHOD:METHOD_CALL:INSERTED:global:reduce, METHOD:METHOD_CALL:REMOVED:global:reduce, METHOD:METHOD_CALL:UPDATED:global:reduce, METHOD:METHOD_CALL:INSERTED:global:indexOf, METHOD:METHOD_CALL:REMOVED:global:indexOf, METHOD:METHOD_CALL:UPDATED:global:indexOf, CLASS:METHOD_CALL:INSERTED:global:Object, CLASS:METHOD_CALL:UPDATED:global:Object, CLASS:METHOD_CALL:REMOVED:global:Object, CLASS:ARGUMENT:REMOVED:global:Object, METHOD:METHOD_CALL:INSERTED:global:substr, METHOD:METHOD_CALL:REMOVED:global:substr, METHOD:METHOD_CALL:UPDATED:global:substr, RESERVED:ASSIGNMENT_LHS:INSERTED:global:interface, RESERVED:ASSIGNMENT_RHS:INSERTED:global:interface, RESERVED:METHOD_CALL:INSERTED:global:interface, METHOD:METHOD_CALL:REMOVED:global:setMilliseconds, METHOD:METHOD_CALL:INSERTED:global:setMilliseconds, RESERVED:ASSIGNMENT_LHS:REMOVED:global:case, RESERVED:METHOD_CALL:REMOVED:global:case, METHOD:METHOD_CALL:REMOVED:global:ify, METHOD:METHOD_CALL:INSERTED:global:ify, METHOD:METHOD_CALL:UPDATED:global:ify, METHOD:ARGUMENT:INSERTED:global:ify, METHOD:METHOD_CALL:REMOVED:global:parse, METHOD:METHOD_CALL:INSERTED:global:parse, METHOD:METHOD_CALL:UPDATED:global:parse, METHOD:METHOD_DECLARATION:INSERTED:global:parse, METHOD:METHOD_CALL:UPDATED:global:unshift, METHOD:METHOD_CALL:INSERTED:global:unshift, METHOD:METHOD_CALL:REMOVED:global:unshift, METHOD:METHOD_CALL:INSERTED:global:keys, METHOD:METHOD_CALL:REMOVED:global:keys, METHOD:METHOD_CALL:UPDATED:global:keys, METHOD:METHOD_CALL:REMOVED:global:isFinite, METHOD:METHOD_CALL:INSERTED:global:isFinite, METHOD:METHOD_DECLARATION:INSERTED:global:isFinite, METHOD:METHOD_DECLARATION:REMOVED:global:isFinite, RESERVED:STATEMENT:INSERTED:global:return, RESERVED:STATEMENT:REMOVED:global:return, RESERVED:STATEMENT:UPDATED:global:return, RESERVED:CONDITION:INSERTED:global:return, RESERVED:STATEMENT:INSERTED:global:break, RESERVED:STATEMENT:REMOVED:global:break, METHOD:METHOD_CALL:REMOVED:global:toLocale, CLASS:ARGUMENT:INSERTED:global:RegExp, CLASS:METHOD_CALL:UPDATED:global:RegExp, CLASS:ARGUMENT:UPDATED:global:RegExp, METHOD:METHOD_CALL:REMOVED:global:defineProperty, METHOD:METHOD_CALL:INSERTED:global:defineProperty, METHOD:METHOD_CALL:UPDATED:global:defineProperty, FIELD:CONDITION:INSERTED:global:source, FIELD:EXPRESSION:INSERTED:global:source, FIELD:EXPRESSION:REMOVED:global:source, FIELD:CONDITION:REMOVED:global:source, FIELD:ASSIGNMENT_RHS:REMOVED:global:source, METHOD:METHOD_CALL:INSERTED:global:getPrototypeOf, FIELD:CONDITION:UPDATED:global:source, FIELD:ASSIGNMENT_LHS:INSERTED:global:source, FIELD:METHOD_CALL:INSERTED:global:source, FIELD:ASSIGNMENT_RHS:INSERTED:global:source, RESERVED:ASSIGNMENT_RHS:REMOVED:global:false, RESERVED:ASSIGNMENT_RHS:INSERTED:global:false, RESERVED:CONDITION:INSERTED:global:false, RESERVED:ARGUMENT:INSERTED:global:false, RESERVED:CONDITION:REMOVED:global:false, RESERVED:ARGUMENT:REMOVED:global:false, RESERVED:EXPRESSION:INSERTED:global:false, METHOD:METHOD_CALL:REMOVED:global:getTimezoneOffset, METHOD:METHOD_CALL:INSERTED:global:getTimezoneOffset, RESERVED:ASSIGNMENT_LHS:UPDATED:global:public, RESERVED:METHOD_CALL:INSERTED:global:public, RESERVED:ASSIGNMENT_LHS:INSERTED:global:public, RESERVED:ASSIGNMENT_LHS:REMOVED:global:this, RESERVED:ASSIGNMENT_LHS:INSERTED:global:this, RESERVED:METHOD_CALL:INSERTED:global:this, RESERVED:ARGUMENT:INSERTED:global:this, RESERVED:ARGUMENT:REMOVED:global:this, RESERVED:ASSIGNMENT_RHS:INSERTED:global:this, RESERVED:ASSIGNMENT_RHS:REMOVED:global:this, RESERVED:METHOD_CALL:REMOVED:global:this, RESERVED:CONDITION:INSERTED:global:this, RESERVED:EXPRESSION:INSERTED:global:this, RESERVED:CONDITION:REMOVED:global:this, RESERVED:EXPRESSION:REMOVED:global:this, METHOD:METHOD_CALL:REMOVED:global:UTC, METHOD:METHOD_CALL:INSERTED:global:toUTC, METHOD:METHOD_CALL:REMOVED:global:toUTC, METHOD:METHOD_CALL:UPDATED:global:toUTC, METHOD:METHOD_CALL:REMOVED:global:setMinutes, METHOD:METHOD_CALL:REMOVED:global:toUpperCase, METHOD:METHOD_CALL:INSERTED:global:toUpperCase, METHOD:METHOD_CALL:UPDATED:global:toUpperCase, RESERVED:ASSIGNMENT_LHS:INSERTED:global:delete, RESERVED:ASSIGNMENT_LHS:UPDATED:global:long, RESERVED:CONDITION:UPDATED:global:long, RESERVED:CONDITION:REMOVED:global:long, RESERVED:METHOD_CALL:UPDATED:global:long, RESERVED:EXPRESSION:INSERTED:global:long, RESERVED:EXPRESSION:REMOVED:global:long, RESERVED:METHOD_CALL:INSERTED:global:delete, RESERVED:ASSIGNMENT_RHS:INSERTED:global:delete, RESERVED:ASSIGNMENT_LHS:UPDATED:global:delete, RESERVED:ASSIGNMENT_LHS:REMOVED:global:delete, RESERVED:METHOD_CALL:UPDATED:global:delete, RESERVED:ASSIGNMENT_LHS:REMOVED:global:long, RESERVED:ASSIGNMENT_LHS:INSERTED:global:long, RESERVED:CONDITION:INSERTED:global:long, RESERVED:METHOD_CALL:REMOVED:global:delete, METHOD:METHOD_CALL:INSERTED:global:sort, METHOD:METHOD_CALL:REMOVED:global:sort, METHOD:METHOD_CALL:UPDATED:global:sort, METHOD:ARGUMENT:REMOVED:global:sort, METHOD:METHOD_CALL:INSERTED:global:max, METHOD:METHOD_CALL:REMOVED:global:max, METHOD:ARGUMENT:INSERTED:global:max, METHOD:METHOD_CALL:UPDATED:global:max, METHOD:METHOD_CALL:INSERTED:global:preventExtensions, METHOD:METHOD_CALL:INSERTED:global:getMilliseconds, METHOD:METHOD_CALL:UPDATED:global:getMilliseconds, CLASS:METHOD_CALL:INSERTED:global:Math, CLASS:METHOD_CALL:REMOVED:global:Math, CLASS:METHOD_CALL:UPDATED:global:Math, METHOD:METHOD_CALL:INSERTED:global:lastIndexOf, METHOD:METHOD_CALL:UPDATED:global:lastIndexOf, METHOD:METHOD_CALL:REMOVED:global:lastIndexOf, FIELD:CONDITION:REMOVED:global:ignoreCase, METHOD:METHOD_CALL:REMOVED:global:slice, METHOD:METHOD_CALL:INSERTED:global:slice, CLASS:METHOD_CALL:INSERTED:global:Array, METHOD:METHOD_CALL:UPDATED:global:slice, CLASS:METHOD_CALL:REMOVED:global:Array, CLASS:METHOD_CALL:UPDATED:global:Array, METHOD:METHOD_CALL:INSERTED:global:getUTCMinutes, RESERVED:CONDITION:UPDATED:global:falsey, RESERVED:CONDITION:INSERTED:global:falsey, RESERVED:CONDITION:REMOVED:global:falsey, RESERVED:ASSIGNMENT_LHS:REMOVED:global:package, RESERVED:EXPRESSION:INSERTED:global:package, RESERVED:ASSIGNMENT_LHS:INSERTED:global:package, RESERVED:ARGUMENT:INSERTED:global:package, RESERVED:CONDITION:INSERTED:global:package, RESERVED:ASSIGNMENT_RHS:INSERTED:global:package, RESERVED:ASSIGNMENT_RHS:UPDATED:global:package, RESERVED:METHOD_CALL:REMOVED:global:package, RESERVED:METHOD_CALL:INSERTED:global:package, RESERVED:VARIABLE_DECLARATION:INSERTED:global:package, RESERVED:CONDITION:REMOVED:global:package, RESERVED:ASSIGNMENT_RHS:REMOVED:global:package, METHOD:METHOD_CALL:REMOVED:global:round, METHOD:METHOD_CALL:INSERTED:global:round, METHOD:METHOD_CALL:UPDATED:global:round, METHOD:METHOD_CALL:UPDATED:global:map, METHOD:METHOD_CALL:INSERTED:global:map, METHOD:METHOD_CALL:REMOVED:global:map, METHOD:ARGUMENT:INSERTED:global:map, METHOD:METHOD_CALL:INSERTED:global:getFullYear, METHOD:METHOD_CALL:REMOVED:global:getFullYear, RESERVED:METHOD_CALL:INSERTED:global:boolean, RESERVED:ASSIGNMENT_LHS:INSERTED:global:boolean, RESERVED:METHOD_CALL:UPDATED:global:boolean, RESERVED:METHOD_CALL:REMOVED:global:boolean, RESERVED:ASSIGNMENT_LHS:REMOVED:global:boolean, RESERVED:METHOD_CALL:INSERTED:global:function, RESERVED:METHOD_CALL:UPDATED:global:switch, FIELD:CONDITION:INSERTED:global:message, FIELD:EXPRESSION:INSERTED:global:message, FIELD:ASSIGNMENT_LHS:INSERTED:global:message, FIELD:ASSIGNMENT_RHS:INSERTED:global:message, FIELD:CONDITION:REMOVED:global:message, FIELD:CONDITION:UPDATED:global:message, FIELD:EXPRESSION:REMOVED:global:message, FIELD:METHOD_CALL:INSERTED:global:message, FIELD:ASSIGNMENT_LHS:REMOVED:global:message, FIELD:METHOD_CALL:UPDATED:global:message, FIELD:ASSIGNMENT_RHS:REMOVED:global:message, RESERVED:ASSIGNMENT_LHS:INSERTED:global:super, METHOD:METHOD_CALL:INSERTED:global:decodeURIComponent, METHOD:METHOD_CALL:REMOVED:global:decodeURIComponent, METHOD:METHOD_CALL:UPDATED:global:decodeURIComponent, RESERVED:CONDITION:UPDATED:global:typeof, RESERVED:CONDITION:REMOVED:global:typeof, RESERVED:ASSIGNMENT_RHS:UPDATED:global:typeof, RESERVED:CONDITION:INSERTED:global:typeof, RESERVED:ARGUMENT:REMOVED:global:typeof, RESERVED:ARGUMENT:INSERTED:global:typeof, RESERVED:ASSIGNMENT_RHS:REMOVED:global:typeof, RESERVED:ARGUMENT:UPDATED:global:typeof, RESERVED:EXPRESSION:INSERTED:global:typeof, RESERVED:ASSIGNMENT_RHS:INSERTED:global:typeof, RESERVED:ASSIGNMENT_LHS:INSERTED:global:typeof, RESERVED:EXPRESSION:UPDATED:global:typeof, CONSTANT:ASSIGNMENT_RHS:REMOVED:global:POSITIVE_INFINITY, METHOD:METHOD_CALL:UPDATED:global:push, METHOD:METHOD_CALL:INSERTED:global:push, METHOD:METHOD_CALL:REMOVED:global:push, RESERVED:ASSIGNMENT_LHS:REMOVED:global:yield, RESERVED:ASSIGNMENT_LHS:INSERTED:global:yield, METHOD:METHOD_CALL:INSERTED:global:setUTCMinutes, CONSTANT:ASSIGNMENT_RHS:REMOVED:global:NEGATIVE_INFINITY, METHOD:METHOD_CALL:INSERTED:global:compile, METHOD:METHOD_CALL:REMOVED:global:compile, METHOD:METHOD_CALL:REMOVED:global:getTime, METHOD:METHOD_CALL:INSERTED:global:getTime, METHOD:METHOD_CALL:UPDATED:global:getTime, RESERVED:ARGUMENT:REMOVED:global:arguments, RESERVED:ARGUMENT:INSERTED:global:arguments, RESERVED:ARGUMENT:UPDATED:global:arguments, RESERVED:EXPRESSION:INSERTED:global:arguments, RESERVED:EXPRESSION:UPDATED:global:arguments, RESERVED:ASSIGNMENT_RHS:INSERTED:global:arguments, RESERVED:METHOD_CALL:INSERTED:global:arguments, RESERVED:CONDITION:UPDATED:global:arguments, RESERVED:CONDITION:INSERTED:global:arguments, RESERVED:EXPRESSION:REMOVED:global:arguments, RESERVED:CONDITION:REMOVED:global:arguments, RESERVED:ASSIGNMENT_LHS:REMOVED:global:arguments, RESERVED:METHOD_CALL:REMOVED:global:arguments, RESERVED:ASSIGNMENT_RHS:REMOVED:global:arguments, RESERVED:ASSIGNMENT_RHS:UPDATED:global:arguments, RESERVED:ASSIGNMENT_LHS:INSERTED:global:arguments, METHOD:METHOD_CALL:UPDATED:global:toLocaleLowerCase, CLASS:ARGUMENT:INSERTED:global:Error, CLASS:METHOD_CALL:INSERTED:global:Error, CLASS:METHOD_CALL:REMOVED:global:Error, METHOD:METHOD_CALL:INSERTED:global:toJSON, METHOD:METHOD_CALL:UPDATED:global:toJSON, METHOD:METHOD_CALL:REMOVED:global:toJSON, RESERVED:ASSIGNMENT_LHS:REMOVED:global:while, RESERVED:METHOD_CALL:REMOVED:global:while, RESERVED:METHOD_CALL:INSERTED:global:instanceof, RESERVED:METHOD_CALL:REMOVED:global:instanceof, METHOD:METHOD_CALL:INSERTED:global:forEach, METHOD:METHOD_CALL:REMOVED:global:forEach, METHOD:METHOD_CALL:UPDATED:global:forEach, RESERVED:CONDITION:UPDATED:global:sheq, RESERVED:CONDITION:REMOVED:global:sheq, RESERVED:CONDITION:INSERTED:global:sheq, RESERVED:METHOD_CALL:INSERTED:global:export, RESERVED:METHOD_CALL:REMOVED:global:export, RESERVED:METHOD_CALL:UPDATED:global:export, RESERVED:ASSIGNMENT_LHS:INSERTED:global:export, RESERVED:ASSIGNMENT_RHS:REMOVED:global:true, RESERVED:ASSIGNMENT_RHS:INSERTED:global:true, RESERVED:CONDITION:INSERTED:global:true, RESERVED:ARGUMENT:INSERTED:global:true, RESERVED:CONDITION:REMOVED:global:true, RESERVED:ARGUMENT:REMOVED:global:true, RESERVED:METHOD_CALL:INSERTED:global:true, RESERVED:EXPRESSION:REMOVED:global:true, RESERVED:EXPRESSION:INSERTED:global:true, FIELD:ASSIGNMENT_LHS:INSERTED:global:lastIndex, FIELD:METHOD_CALL:REMOVED:global:global, FIELD:ASSIGNMENT_LHS:REMOVED:global:global, FIELD:ASSIGNMENT_LHS:INSERTED:global:global, FIELD:ASSIGNMENT_RHS:INSERTED:global:global, FIELD:ASSIGNMENT_RHS:REMOVED:global:global, FIELD:METHOD_CALL:INSERTED:global:global, FIELD:ARGUMENT:REMOVED:global:global, FIELD:EXPRESSION:INSERTED:global:global, FIELD:ASSIGNMENT_LHS:UPDATED:global:global, FIELD:CONDITION:INSERTED:global:global, FIELD:CONDITION:REMOVED:global:global, FIELD:ASSIGNMENT_RHS:UPDATED:global:global, METHOD:METHOD_CALL:INSERTED:global:concat, METHOD:METHOD_CALL:REMOVED:global:concat, METHOD:METHOD_CALL:UPDATED:global:concat, RESERVED:METHOD_CALL:INSERTED:global:throw, METHOD:METHOD_CALL:INSERTED:global:localeCompare, RESERVED:ASSIGNMENT_LHS:INSERTED:global:int, METHOD:METHOD_CALL:INSERTED:global:match, METHOD:METHOD_CALL:REMOVED:global:match, METHOD:METHOD_CALL:UPDATED:global:match, METHOD:METHOD_CALL:INSERTED:global:encodeURI, METHOD:METHOD_CALL:REMOVED:global:encodeURI, METHOD:METHOD_CALL:UPDATED:global:encodeURI, RESERVED:STATEMENT:UPDATED:global:continue, RESERVED:STATEMENT:REMOVED:global:continue, RESERVED:STATEMENT:INSERTED:global:continue, RESERVED:ARGUMENT:INSERTED:global:null, RESERVED:ASSIGNMENT_RHS:INSERTED:global:null, RESERVED:CONDITION:INSERTED:global:null, RESERVED:ASSIGNMENT_LHS:REMOVED:global:null, RESERVED:CONDITION:REMOVED:global:null, RESERVED:ASSIGNMENT_RHS:REMOVED:global:null, RESERVED:ARGUMENT:REMOVED:global:null, METHOD:METHOD_CALL:INSERTED:global:getUTCSeconds, METHOD:METHOD_CALL:INSERTED:global:hasOwnProperty, METHOD:METHOD_CALL:REMOVED:global:hasOwnProperty, METHOD:ARGUMENT:INSERTED:global:hasOwnProperty, METHOD:METHOD_CALL:UPDATED:global:hasOwnProperty, METHOD:METHOD_CALL:UPDATED:global:parseFloat, METHOD:METHOD_CALL:INSERTED:global:parseFloat, METHOD:METHOD_CALL:REMOVED:global:parseFloat, METHOD:METHOD_CALL:INSERTED:global:abs, METHOD:METHOD_CALL:REMOVED:global:abs, RESERVED:ARGUMENT:UPDATED:global:undefined, RESERVED:CONDITION:REMOVED:global:undefined, RESERVED:ASSIGNMENT_RHS:REMOVED:global:undefined, RESERVED:ASSIGNMENT_RHS:INSERTED:global:undefined, RESERVED:ASSIGNMENT_LHS:REMOVED:global:undefined, RESERVED:ASSIGNMENT_RHS:UPDATED:global:undefined, RESERVED:CONDITION:INSERTED:global:undefined, RESERVED:ARGUMENT:REMOVED:global:undefined, RESERVED:CONDITION:UPDATED:global:undefined, RESERVED:VARIABLE_DECLARATION:REMOVED:global:undefined, RESERVED:PARAMETER_DECLARATION:UPDATED:global:undefined, RESERVED:ARGUMENT:INSERTED:global:undefined, RESERVED:VARIABLE_DECLARATION:INSERTED:global:undefined, RESERVED:PARAMETER_DECLARATION:REMOVED:global:undefined, FIELD:CONDITION:INSERTED:global:constructor, FIELD:METHOD_CALL:UPDATED:global:constructor, FIELD:ARGUMENT:REMOVED:global:constructor, FIELD:ARGUMENT:UPDATED:global:constructor, FIELD:EXPRESSION:INSERTED:global:constructor, FIELD:CONDITION:REMOVED:global:constructor, FIELD:ASSIGNMENT_RHS:REMOVED:global:constructor, FIELD:ASSIGNMENT_RHS:INSERTED:global:constructor, FIELD:ASSIGNMENT_LHS:REMOVED:global:constructor, FIELD:ARGUMENT:INSERTED:global:constructor, FIELD:ASSIGNMENT_LHS:INSERTED:global:constructor, METHOD:METHOD_CALL:REMOVED:global:bind, METHOD:METHOD_CALL:INSERTED:global:bind, METHOD:METHOD_CALL:UPDATED:global:bind, METHOD:ARGUMENT:REMOVED:global:bind, METHOD:ARGUMENT:INSERTED:global:bind, METHOD:METHOD_CALL:INSERTED:global:unescape, METHOD:ARGUMENT:INSERTED:global:unescape, METHOD:METHOD_CALL:INSERTED:global:shift, METHOD:METHOD_CALL:REMOVED:global:shift, METHOD:METHOD_CALL:UPDATED:global:shift, METHOD:METHOD_CALL:INSERTED:global:setHours, METHOD:METHOD_CALL:UPDATED:global:setHours, METHOD:METHOD_CALL:INSERTED:global:trim, METHOD:METHOD_CALL:REMOVED:global:trim, METHOD:METHOD_CALL:UPDATED:global:trim, METHOD:METHOD_CALL:REMOVED:global:setHours, METHOD:METHOD_CALL:REMOVED:global:apply, METHOD:METHOD_CALL:INSERTED:global:apply, METHOD:METHOD_CALL:UPDATED:global:apply, METHOD:METHOD_CALL:INSERTED:global:getUTCMonth, RESERVED:ASSIGNMENT_LHS:INSERTED:global:else, RESERVED:CONDITION:INSERTED:global:else, RESERVED:CONDITION:UPDATED:global:short, RESERVED:ASSIGNMENT_LHS:UPDATED:global:short, RESERVED:ASSIGNMENT_LHS:REMOVED:global:short, RESERVED:ASSIGNMENT_LHS:INSERTED:global:short, RESERVED:METHOD_CALL:INSERTED:global:short, CLASS:METHOD_CALL:REMOVED:global:JSON, CLASS:METHOD_CALL:INSERTED:global:JSON, CLASS:METHOD_CALL:UPDATED:global:JSON, CLASS:ARGUMENT:INSERTED:global:JSON, METHOD:METHOD_CALL:REMOVED:global:getHours, METHOD:METHOD_CALL:INSERTED:global:getHours, METHOD:METHOD_CALL:INSERTED:global:toGMT, METHOD:METHOD_CALL:UPDATED:global:toGMT, METHOD:METHOD_CALL:INSERTED:global:to, METHOD:METHOD_CALL:REMOVED:global:to, METHOD:METHOD_CALL:UPDATED:global:to, RESERVED:ASSIGNMENT_LHS:REMOVED:global:native, METHOD:METHOD_CALL:INSERTED:global:getDate, METHOD:METHOD_CALL:REMOVED:global:getDate, METHOD:METHOD_CALL:INSERTED:global:is, METHOD:METHOD_CALL:REMOVED:global:is, METHOD:METHOD_CALL:UPDATED:global:is, METHOD:METHOD_CALL:INSERTED:global:sub, METHOD:METHOD_CALL:REMOVED:global:sub, METHOD:METHOD_CALL:UPDATED:global:sub, METHOD:METHOD_CALL:REMOVED:global:getDay, METHOD:METHOD_CALL:INSERTED:global:charAt, METHOD:METHOD_CALL:REMOVED:global:charAt, METHOD:METHOD_CALL:UPDATED:global:charAt, RESERVED:CONDITION:INSERTED:global:eq, RESERVED:CONDITION:UPDATED:global:eq, RESERVED:CONDITION:REMOVED:global:eq, RESERVED:STATEMENT:INSERTED:global:var, RESERVED:STATEMENT:UPDATED:global:var, RESERVED:STATEMENT:REMOVED:global:var, METHOD:METHOD_CALL:REMOVED:global:toLocaleDate, METHOD:METHOD_CALL:INSERTED:global:toFixed, METHOD:METHOD_CALL:REMOVED:global:toFixed, RESERVED:METHOD_CALL:INSERTED:global:eval, RESERVED:METHOD_CALL:REMOVED:global:eval, RESERVED:ASSIGNMENT_LHS:REMOVED:global:eval, RESERVED:ASSIGNMENT_LHS:INSERTED:global:eval, METHOD:ARGUMENT:REMOVED:global:search, METHOD:METHOD_CALL:INSERTED:global:search, METHOD:METHOD_CALL:REMOVED:global:search, METHOD:METHOD_CALL:UPDATED:global:search, RESERVED:ASSIGNMENT_LHS:REMOVED:global:with, RESERVED:METHOD_CALL:INSERTED:global:with, METHOD:METHOD_CALL:INSERTED:global:Number, METHOD:METHOD_CALL:REMOVED:global:Number, METHOD:METHOD_CALL:UPDATED:global:Number, RESERVED:METHOD_CALL:UPDATED:global:throws, RESERVED:METHOD_CALL:REMOVED:global:throws, RESERVED:CONDITION:INSERTED:global:throws, RESERVED:CONDITION:REMOVED:global:throws, RESERVED:ASSIGNMENT_LHS:REMOVED:global:throws, METHOD:METHOD_CALL:INSERTED:global:getOwnPropertyNames, METHOD:METHOD_CALL:UPDATED:global:getOwnPropertyNames, RESERVED:ASSIGNMENT_LHS:REMOVED:global:in, RESERVED:ARGUMENT:REMOVED:global:in, RESERVED:ASSIGNMENT_LHS:INSERTED:global:in, RESERVED:METHOD_CALL:UPDATED:global:in, METHOD:METHOD_CALL:INSERTED:global:every, METHOD:METHOD_CALL:REMOVED:global:every, METHOD:METHOD_CALL:INSERTED:global:getUTCHours, METHOD:METHOD_CALL:INSERTED:global:parseInt, METHOD:METHOD_CALL:REMOVED:global:parseInt, METHOD:METHOD_CALL:UPDATED:global:parseInt, FIELD:CONDITION:REMOVED:global:length, FIELD:ASSIGNMENT_RHS:INSERTED:global:length, FIELD:CONDITION:INSERTED:global:length, FIELD:EXPRESSION:INSERTED:global:length, FIELD:EXPRESSION:REMOVED:global:length, FIELD:ASSIGNMENT_LHS:INSERTED:global:length, FIELD:ASSIGNMENT_RHS:UPDATED:global:length, FIELD:ASSIGNMENT_LHS:REMOVED:global:length, FIELD:METHOD_CALL:REMOVED:global:length, FIELD:CONDITION:UPDATED:global:length, FIELD:ASSIGNMENT_LHS:UPDATED:global:length, FIELD:ASSIGNMENT_RHS:REMOVED:global:length, FIELD:EXPRESSION:UPDATED:global:length, RESERVED:PARAMETER_DECLARATION:INSERTED:global:callback, RESERVED:ARGUMENT:REMOVED:global:callback, RESERVED:ARGUMENT:INSERTED:global:callback, RESERVED:EXPRESSION:INSERTED:global:callback, RESERVED:ASSIGNMENT_RHS:UPDATED:global:callback, RESERVED:ASSIGNMENT_LHS:INSERTED:global:callback, RESERVED:METHOD_CALL:UPDATED:global:callback, RESERVED:PARAMETER_DECLARATION:UPDATED:global:callback, RESERVED:METHOD_CALL:INSERTED:global:callback, RESERVED:ASSIGNMENT_RHS:REMOVED:global:callback, RESERVED:CONDITION:INSERTED:global:callback, RESERVED:METHOD_CALL:REMOVED:global:callback, RESERVED:VARIABLE_DECLARATION:INSERTED:global:callback, RESERVED:ASSIGNMENT_RHS:INSERTED:global:callback, RESERVED:CONDITION:REMOVED:global:callback, RESERVED:EXPRESSION:REMOVED:global:callback, RESERVED:ASSIGNMENT_LHS:REMOVED:global:callback, RESERVED:ARGUMENT:UPDATED:global:callback, RESERVED:VARIABLE_DECLARATION:REMOVED:global:callback, RESERVED:PARAMETER_DECLARATION:REMOVED:global:callback, METHOD:METHOD_CALL:INSERTED:global:toISO, METHOD:METHOD_CALL:UPDATED:global:toISO, METHOD:METHOD_CALL:UPDATED:global:filter, METHOD:METHOD_CALL:INSERTED:global:filter, METHOD:METHOD_CALL:REMOVED:global:filter, METHOD:METHOD_CALL:INSERTED:global:getOwnPropertyDescriptor, METHOD:METHOD_CALL:REMOVED:global:getOwnPropertyDescriptor, RESERVED:ASSIGNMENT_LHS:INSERTED:global:extends, RESERVED:ASSIGNMENT_LHS:REMOVED:global:extends, RESERVED:METHOD_CALL:REMOVED:global:extends, CLASS:METHOD_CALL:INSERTED:global:Date, CLASS:METHOD_CALL:REMOVED:global:Date, CLASS:ARGUMENT:INSERTED:global:Date, METHOD:METHOD_CALL:INSERTED:global:replace, METHOD:METHOD_CALL:REMOVED:global:replace, METHOD:METHOD_CALL:UPDATED:global:replace, RESERVED:METHOD_CALL:UPDATED:global:finally, RESERVED:METHOD_CALL:INSERTED:global:finally, METHOD:METHOD_CALL:REMOVED:global:getSeconds, METHOD:METHOD_CALL:INSERTED:global:reverse, CLASS:METHOD_CALL:REMOVED:global:, CLASS:ARGUMENT:UPDATED:global:, METHOD:METHOD_CALL:REMOVED:global:reverse, CLASS:METHOD_CALL:INSERTED:global:, METHOD:METHOD_DECLARATION:REMOVED:global:reverse, CLASS:ARGUMENT:REMOVED:global:, METHOD:METHOD_CALL:UPDATED:global:reverse, METHOD:METHOD_CALL:INSERTED:global:valueOf, METHOD:METHOD_CALL:UPDATED:global:valueOf, METHOD:METHOD_CALL:REMOVED:global:valueOf, METHOD:ARGUMENT:INSERTED:global:valueOf, METHOD:METHOD_CALL:INSERTED:global:getUTCDate, METHOD:METHOD_CALL:REMOVED:global:setSeconds, METHOD:METHOD_CALL:INSERTED:global:setSeconds, RESERVED:CONDITION:INSERTED:global:default, RESERVED:ASSIGNMENT_LHS:REMOVED:global:default, RESERVED:ARGUMENT:INSERTED:global:default, RESERVED:ASSIGNMENT_LHS:INSERTED:global:default, RESERVED:ASSIGNMENT_RHS:REMOVED:global:default, RESERVED:ASSIGNMENT_LHS:UPDATED:global:default, RESERVED:ASSIGNMENT_RHS:INSERTED:global:default, RESERVED:METHOD_CALL:INSERTED:global:default, RESERVED:EXPRESSION:INSERTED:global:default, RESERVED:CONDITION:REMOVED:global:default, RESERVED:METHOD_CALL:REMOVED:global:default, RESERVED:METHOD_CALL:UPDATED:global:default, METHOD:METHOD_CALL:REMOVED:global:getMinutes, RESERVED:METHOD_CALL:REMOVED:global:catch, RESERVED:METHOD_CALL:INSERTED:global:catch, RESERVED:METHOD_CALL:UPDATED:global:catch, RESERVED:ASSIGNMENT_LHS:INSERTED:global:catch, CLASS:METHOD_CALL:REMOVED:global:Number, CLASS:METHOD_CALL:INSERTED:global:Number, CLASS:ARGUMENT:REMOVED:global:Number, METHOD:METHOD_CALL:REMOVED:global:fromCharCode, METHOD:METHOD_CALL:INSERTED:global:fromCharCode, FIELD:METHOD_CALL:REMOVED:global:prototype, FIELD:ASSIGNMENT_LHS:INSERTED:global:prototype, FIELD:METHOD_CALL:INSERTED:global:prototype, FIELD:ASSIGNMENT_LHS:REMOVED:global:prototype, FIELD:CONDITION:INSERTED:global:prototype, FIELD:CONDITION:REMOVED:global:prototype, FIELD:ASSIGNMENT_LHS:UPDATED:global:prototype, FIELD:ASSIGNMENT_RHS:INSERTED:global:prototype, FIELD:METHOD_CALL:UPDATED:global:prototype, FIELD:ASSIGNMENT_RHS:REMOVED:global:prototype, FIELD:CONDITION:UPDATED:global:prototype, FIELD:ARGUMENT:INSERTED:global:prototype, FIELD:ARGUMENT:REMOVED:global:prototype, RESERVED:ASSIGNMENT_LHS:REMOVED:global:let, RESERVED:ASSIGNMENT_LHS:INSERTED:global:let, METHOD:METHOD_CALL:INSERTED:global:getMonth, METHOD:METHOD_CALL:REMOVED:global:splice, METHOD:METHOD_CALL:INSERTED:global:splice, METHOD:METHOD_CALL:UPDATED:global:splice, METHOD:METHOD_CALL:REMOVED:global:getMonth, METHOD:METHOD_CALL:UPDATED:global:some, METHOD:METHOD_CALL:REMOVED:global:some, METHOD:METHOD_CALL:INSERTED:global:some, METHOD:METHOD_CALL:INSERTED:global:ceil, METHOD:METHOD_CALL:INSERTED:global:getUTCDay, METHOD:METHOD_CALL:INSERTED:global:split, METHOD:METHOD_CALL:REMOVED:global:split, METHOD:METHOD_CALL:UPDATED:global:split, METHOD:METHOD_CALL:INSERTED:global:random, METHOD:METHOD_CALL:REMOVED:global:random, METHOD:METHOD_CALL:UPDATED:global:random, METHOD:METHOD_CALL:INSERTED:global:encodeURIComponent, METHOD:METHOD_CALL:REMOVED:global:encodeURIComponent, RESERVED:PARAMETER_DECLARATION:INSERTED:global:volatile, RESERVED:CONDITION:INSERTED:global:volatile, RESERVED:VARIABLE_DECLARATION:INSERTED:global:volatile, RESERVED:ARGUMENT:INSERTED:global:volatile, METHOD:METHOD_CALL:INSERTED:global:toLowerCase, METHOD:METHOD_CALL:REMOVED:global:toLowerCase, METHOD:METHOD_CALL:UPDATED:global:toLowerCase, METHOD:METHOD_CALL:REMOVED:global:setTime, METHOD:METHOD_CALL:INSERTED:global:setTime, METHOD:METHOD_CALL:INSERTED:global:isNaN, METHOD:METHOD_CALL:REMOVED:global:isNaN, METHOD:METHOD_CALL:UPDATED:global:isNaN, RESERVED:METHOD_CALL:INSERTED:global:static, RESERVED:METHOD_CALL:REMOVED:global:static, RESERVED:VARIABLE_DECLARATION:INSERTED:global:static, RESERVED:ASSIGNMENT_LHS:UPDATED:global:static, RESERVED:METHOD_CALL:UPDATED:global:static, RESERVED:CONDITION:UPDATED:global:static, RESERVED:METHOD_DECLARATION:REMOVED:global:static, RESERVED:ASSIGNMENT_RHS:REMOVED:global:static, METHOD:METHOD_CALL:INSERTED:global:propertyIsEnumerable, METHOD:METHOD_CALL:REMOVED:global:propertyIsEnumerable, RESERVED:STATEMENT:INSERTED:global:try, RESERVED:STATEMENT:REMOVED:global:try, RESERVED:STATEMENT:UPDATED:global:try, FIELD:EXPRESSION:REMOVED:global:name, FIELD:ASSIGNMENT_RHS:INSERTED:global:name, FIELD:ASSIGNMENT_RHS:REMOVED:global:name, FIELD:METHOD_CALL:REMOVED:global:name, FIELD:CONDITION:INSERTED:global:name, FIELD:ASSIGNMENT_LHS:REMOVED:global:name, FIELD:CONDITION:REMOVED:global:name, FIELD:EXPRESSION:INSERTED:global:name, FIELD:CONDITION:UPDATED:global:name, FIELD:ASSIGNMENT_RHS:UPDATED:global:name, FIELD:ASSIGNMENT_LHS:INSERTED:global:name, FIELD:ASSIGNMENT_LHS:UPDATED:global:name, FIELD:EXPRESSION:UPDATED:global:name, FIELD:METHOD_CALL:INSERTED:global:name, RESERVED:CONDITION:INSERTED:global:char, RESERVED:ASSIGNMENT_RHS:UPDATED:global:char, RESERVED:ASSIGNMENT_RHS:REMOVED:global:char, RESERVED:ASSIGNMENT_RHS:INSERTED:global:char, METHOD:METHOD_CALL:INSERTED:global:floor, METHOD:METHOD_CALL:REMOVED:global:floor, METHOD:METHOD_CALL:INSERTED:global:test, METHOD:METHOD_CALL:UPDATED:global:test, METHOD:METHOD_CALL:REMOVED:global:test, METHOD:ARGUMENT:INSERTED:global:test, METHOD:ARGUMENT:UPDATED:global:test, METHOD:ARGUMENT:REMOVED:global:test, METHOD:METHOD_CALL:INSERTED:global:create, METHOD:METHOD_CALL:INSERTED:global:, METHOD:METHOD_CALL:REMOVED:global:, METHOD:METHOD_CALL:REMOVED:global:create, METHOD:ARGUMENT:INSERTED:global:create, METHOD:METHOD_CALL:UPDATED:global:create, METHOD:METHOD_DECLARATION:UPDATED:global:create, METHOD:METHOD_CALL:UPDATED:global:, RESERVED:ASSIGNMENT_RHS:REMOVED:global:error, RESERVED:CONDITION:INSERTED:global:error, RESERVED:ARGUMENT:UPDATED:global:error, RESERVED:CONDITION:REMOVED:global:error, RESERVED:ARGUMENT:INSERTED:global:error, RESERVED:PARAMETER_DECLARATION:REMOVED:global:error, RESERVED:PARAMETER_DECLARATION:UPDATED:global:error, RESERVED:EXPRESSION:REMOVED:global:error, RESERVED:EXCEPTION_CATCH:INSERTED:global:error, RESERVED:EXPRESSION:INSERTED:global:error, RESERVED:ASSIGNMENT_RHS:INSERTED:global:error, RESERVED:METHOD_DECLARATION:INSERTED:global:error, RESERVED:VARIABLE_DECLARATION:UPDATED:global:error, RESERVED:VARIABLE_DECLARATION:REMOVED:global:error, RESERVED:EXCEPTION_CATCH:UPDATED:global:error, RESERVED:METHOD_CALL:UPDATED:global:error, RESERVED:ASSIGNMENT_LHS:UPDATED:global:error, RESERVED:ARGUMENT:REMOVED:global:error, RESERVED:ASSIGNMENT_LHS:REMOVED:global:error, RESERVED:EXCEPTION_CATCH:REMOVED:global:error, RESERVED:ASSIGNMENT_RHS:UPDATED:global:error, RESERVED:METHOD_CALL:INSERTED:global:error, RESERVED:PARAMETER_DECLARATION:INSERTED:global:error, RESERVED:VARIABLE_DECLARATION:INSERTED:global:error, RESERVED:EXPRESSION:UPDATED:global:error, RESERVED:ASSIGNMENT_LHS:INSERTED:global:error, RESERVED:CONDITION:UPDATED:global:error, RESERVED:METHOD_CALL:REMOVED:global:error, METHOD:METHOD_CALL:INSERTED:global:charCodeAt, METHOD:METHOD_CALL:REMOVED:global:charCodeAt, RESERVED:ASSIGNMENT_LHS:INSERTED:global:new, RESERVED:STATEMENT:INSERTED:global:new, RESERVED:STATEMENT:UPDATED:global:new, RESERVED:STATEMENT:REMOVED:global:new, RESERVED:CONDITION:INSERTED:global:new, RESERVED:EXPRESSION:INSERTED:global:new, RESERVED:CONDITION:UPDATED:global:new, RESERVED:CONDITION:REMOVED:global:new, RESERVED:ASSIGNMENT_LHS:REMOVED:global:new, METHOD:METHOD_CALL:INSERTED:global:decodeURI, FIELD:CONDITION:REMOVED:global:multiline, RESERVED:ASSIGNMENT_LHS:REMOVED:global:import, RESERVED:ARGUMENT:REMOVED:global:import, RESERVED:ASSIGNMENT_RHS:REMOVED:global:import, METHOD:METHOD_CALL:INSERTED:global:exec, METHOD:METHOD_CALL:REMOVED:global:exec, METHOD:METHOD_CALL:UPDATED:global:exec, METHOD:ARGUMENT:INSERTED:global:exec, RESERVED:ASSIGNMENT_LHS:INSERTED:global:private, RESERVED:CONDITION:INSERTED:global:private ");
			sb.append(featureVectorHeader);
			
			for (Instance dataset: dataSetWeka){
				sb.append("\n");
				sb.append(dataset.toString());
			}
			
			// Colocar o DataSet no csv
			PrintWriter pw = new PrintWriter(new File("dataset_bugid_with_header.csv"));
			pw.write(sb.toString());
	        pw.close();
	        System.out.println("done!");
			
			
			
			clusteringDataSet.getWekaClusters(clusterMetrics);
			
			
		} catch (WekaException ex) {
			logger.error("Weka error on building clusters.", ex);
			return;
		}

		/* Save arff file */
		if (options.getArffFolder() != null)
			clusteringDataSet.writeArffFile(options.getArffFolder(), "ALL_KEYWORDS.arff");

		/* We only have one ClusterMetrics now. */
		keywordClusters.add(clusterMetrics);

		/* Write results from the clustering. */
		System.out.println(clusterMetrics.getRankedClusterTable());
		
		
		
		// Obtendo os dados dos clusters gerados
		Collection<Cluster> clustersTotais = clusterMetrics.clusters.values();
		
		exportResultDBScanWithInstances(clustersTotais);

		/* Write the evaluation results from the clustering. */
		//EvaluationResult result = clusteringDataSet.evaluate(clusterMetrics);
		//System.out.println(result.getConfusionMatrix());

		System.out.println(clusterMetrics.getLatexTable(keywordClusters));
	}

	
	/**
	 * Método para exportar o resultado obtido do DBScan com as devidas instancias
	 * @param clustersTotais Collection of Cluster with all instances
	 * @author Charles Mendes
	 * @throws FileNotFoundException 
	 */
	private static void exportResultDBScanWithInstances(Collection<Cluster> clustersTotais) 
			throws FileNotFoundException {
		
		// Instância de String Builder
		StringBuilder sb = new StringBuilder();
		
		sb.append("ClusterID; BCTs; BasicChanges; Complexity; ProjectCount; Instances; ContainsInstances");
		
		Boolean isFirst = true;
		
		for(Cluster ctr : clustersTotais) {
			isFirst = true;
			sb.append("\n");
			
			//ClusterID
			sb.append(ctr.cluster);
			sb.append(" ; ");
			//BCTs
			
			for (Map.Entry<String, Integer> entry : ctr.keywords.entrySet()) {
				if(!isFirst)
					sb.append(" , ");
				
				sb.append((String)entry.getKey());
				isFirst = false;
			}
			
			//sb.append(ctr.keywords.toString().replace("{", "").replace("}", ""));
			sb.append(" ; ");
			//BasicChanges
			sb.append(ctr.keywords.size());
			sb.append(" ; ");
			//Complexity
			sb.append(ctr.getAverageModifiedStatements());
			sb.append(" ; ");
			//ProjectCount
			sb.append(ctr.projects.size());
			sb.append(" ; ");			
			//Instances
			sb.append(ctr.instances.size());
			sb.append(" ; ");
			//ContainsInstances
			sb.append(ctr.instances.toString().replace("=?", "").replace("{", "").replace("}", ""));
		}
		
		// Colocar o DataSet no csv
		PrintWriter pw = new PrintWriter(new File("dataset_result_DBScan_with_instances.csv"));
		pw.write(sb.toString());
        pw.close();
        System.out.println("Done Export Result DBScan!");
		
	}
	
	
	
	/**
	 * Prints the help file for main.
	 * @param parser The args4j parser.
	 */
	private static void printHelp(CmdLineParser parser) {
        System.out.print("Usage: DataSetMain ");
        parser.printSingleLineUsage(System.out);
        System.out.println("\n");
        parser.printUsage(System.out);
        System.out.println("");
        return;
	}

	/**
	 * Prints the usage of main.
	 * @param error The error message that triggered the usage message.
	 * @param parser The args4j parser.
	 */
	private static void printUsage(String error, CmdLineParser parser) {
        System.out.println(error);
        System.out.print("Usage: DataSetMain ");
        parser.printSingleLineUsage(System.out);
        System.out.println("");
        return;
	}

	/**
	 * Selects feature vectors with:
	 *  - Complexity <= {@code complexity}
	 *  - Commit message != MERGE
	 * @param maxComplexity The maximum complexity for the feature vector.
	 * @return The Datalog query that selects which rows to data mine.
	 */
	public static IQuery getBasicRowFilterQuery(Integer maxComplexity) {

		IVariable complexity = Factory.TERM.createVariable("Complexity");

		IQuery query =
			Factory.BASIC.createQuery(
				Factory.BASIC.createLiteral(true,
					Factory.BASIC.createPredicate("FeatureVector", 8),
					Factory.BASIC.createTuple(
						Factory.TERM.createVariable("ID"),
						Factory.TERM.createVariable("CommitMessage"),
						Factory.TERM.createVariable("URL"),
						Factory.TERM.createVariable("BuggyCommitID"),
						Factory.TERM.createVariable("RepairedCommitID"),
						Factory.TERM.createVariable("Class"),
						Factory.TERM.createVariable("Method"),
						complexity)),
				Factory.BASIC.createLiteral(true,
					Factory.BUILTIN.createLessEqual(
						complexity,
						Factory.CONCRETE.createInt(maxComplexity))),
				Factory.BASIC.createLiteral(true,
					Factory.BUILTIN.createGreater(
						complexity,
						Factory.CONCRETE.createInt(0))),
				Factory.BASIC.createLiteral(true,
					Factory.BUILTIN.createNotExactEqual(
						Factory.TERM.createVariable("CommitMessage"),
						Factory.TERM.createString(Type.MERGE.toString()))));
//				Factory.BASIC.createLiteral(true,
//					Factory.BUILTIN.createEqual(
//						Factory.TERM.createVariable("CommitMessage"),
//						Factory.TERM.createString(Type.BUG_FIX.toString())))

		return query;

	}

	/**
	 * Selects feature vectors with:
	 *  - At least one keyword with context != STATEMENT
	 * @param maxComplexity The maximum complexity for the feature vector.
	 * @return The Datalog query that selects which rows to data mine.
	 */
	public static IQuery getStatementRowFilterQuery(Integer maxComplexity) {

		IVariable complexity = Factory.TERM.createVariable("Complexity");

		IQuery query =
			Factory.BASIC.createQuery(
				Factory.BASIC.createLiteral(true,
					Factory.BASIC.createPredicate("FeatureVector", 8),
					Factory.BASIC.createTuple(
						Factory.TERM.createVariable("ID"),
						Factory.TERM.createVariable("CommitMessage"),
						Factory.TERM.createVariable("URL"),
						Factory.TERM.createVariable("BuggyCommitID"),
						Factory.TERM.createVariable("RepairedCommitID"),
						Factory.TERM.createVariable("Class"),
						Factory.TERM.createVariable("Method"),
						complexity)),
				Factory.BASIC.createLiteral(true,
					Factory.BASIC.createPredicate("KeywordChange", 7),
					Factory.BASIC.createTuple(
						Factory.TERM.createVariable("ID"),
						Factory.TERM.createVariable("KeywordType"),
						Factory.TERM.createVariable("KeywordContext"),
						Factory.TERM.createVariable("ChangeType"),
						Factory.TERM.createVariable("Package"),
						Factory.TERM.createVariable("Keyword"),
						Factory.TERM.createVariable("Count"))),
				Factory.BASIC.createLiteral(true,
					Factory.BUILTIN.createNotExactEqual(
						Factory.TERM.createVariable("KeywordContext"),
						Factory.TERM.createString(KeywordContext.STATEMENT.toString()))),
				Factory.BASIC.createLiteral(true,
					Factory.BUILTIN.createNotExactEqual(
						Factory.TERM.createVariable("ChangeType"),
						Factory.TERM.createString(ChangeType.UPDATED.toString()))));

		return query;

	}

}