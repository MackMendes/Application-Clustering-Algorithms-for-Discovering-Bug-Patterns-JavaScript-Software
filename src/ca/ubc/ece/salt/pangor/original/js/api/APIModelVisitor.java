package ca.ubc.ece.salt.pangor.original.js.api;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.original.api.KeywordDefinition;
import ca.ubc.ece.salt.pangor.original.api.KeywordUse;
import ca.ubc.ece.salt.pangor.original.js.analysis.utilities.SpecialTypeAnalysisUtilities;
import ca.ubc.ece.salt.pangor.original.js.api.JSAPIUtilities;
import java.util.HashMap;
import java.util.Map;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.UnaryExpression;
import org.mozilla.javascript.ast.VariableDeclaration;

public class APIModelVisitor
implements NodeVisitor {
    private Map<KeywordUse, Integer> keywordMap = new HashMap<KeywordUse, Integer>();
    private ScriptNode root;
    private boolean visitFunctions;

    public static Map<KeywordUse, Integer> getScriptFeatureVector(AstRoot script) {
        APIModelVisitor visitor = new APIModelVisitor((ScriptNode)script, true);
        script.visit((NodeVisitor)visitor);
        return visitor.getKeywordMap();
    }

    private APIModelVisitor(ScriptNode root, boolean visitFunctions) {
        this.root = root;
        this.visitFunctions = visitFunctions;
    }

    public Map<KeywordUse, Integer> getKeywordMap() {
        return this.keywordMap;
    }

    public boolean visit(AstNode node) {
        this.registerKeyword(node, node.getChangeType());
        if ((!this.visitFunctions) && ((node instanceof FunctionNode)) && (node != this.root)) {
            return false;
        }
        return true;
    }

    private void registerKeyword(AstNode node, ClassifiedASTNode.ChangeType changeType) {
        KeywordUse keyword;
        KeywordDefinition.KeywordType type;
        String token;
        KeywordUse.KeywordContext context;
        token = "";
        type = JSAPIUtilities.getTokenType(node);
        context = JSAPIUtilities.getTokenContext(node);
        if ((type == KeywordDefinition.KeywordType.UNKNOWN) || (context == KeywordUse.KeywordContext.UNKNOWN)) {
            return;
        }
        if (changeType == ClassifiedASTNode.ChangeType.MOVED) {
            changeType = ClassifiedASTNode.ChangeType.UNCHANGED;
        }
        if (SpecialTypeAnalysisUtilities.isFalsey(node)) {
            keyword = null;
            keyword = new KeywordUse(type, context, "falsey", changeType);
            keyword.apiPackage = "global";
            if (keyword != null) {
                this.increment(keyword);
            }
        }
        if (node instanceof ReturnStatement) {
            token = "return";
        } else if (node instanceof BreakStatement) {
            token = "break";
        } else if (node instanceof ContinueStatement) {
            token = "continue";
        } else if (node instanceof VariableDeclaration) {
            token = "var";
        } else if (node instanceof NewExpression) {
            token = "new";
        } else if (node instanceof TryStatement) {
            token = "try";
        } else if (node instanceof Name) {
            Name name = (Name)node;
            token = name.getIdentifier();
            if (token.matches("e|err|error|exception")) {
                type = KeywordDefinition.KeywordType.RESERVED;
                token = "error";
            } else if (token.matches("cb|callb|callback")) {
                type = KeywordDefinition.KeywordType.RESERVED;
                token = "callback";
            }
        } else if (node instanceof KeywordLiteral) {
            KeywordLiteral kl = (KeywordLiteral)node;
            token = kl.toSource();
        } else if (node instanceof NumberLiteral) {
            NumberLiteral nl = (NumberLiteral)node;
            try {
                if (Double.parseDouble(nl.getValue()) == 0.0) {
                    token = "zero";
                }
            }
            catch (NumberFormatException numberFormatException) {}
        } else if (node instanceof StringLiteral) {
            StringLiteral sl = (StringLiteral)node;
            token = sl.getValue().isEmpty() ? "blank" : sl.getValue();
        } else if (node instanceof UnaryExpression) {
            UnaryExpression ue = (UnaryExpression)node;
            switch (ue.getOperator()) {
                case 32: {
                    token = "typeof";
                }
            }
        } else if (node instanceof InfixExpression) {
            InfixExpression ie = (InfixExpression)node;
            if ((ie.getType() == 46) || (ie.getType() == 47))
            {
              if ((SpecialTypeAnalysisUtilities.getSpecialType(ie.getLeft()) != null) || 
                (SpecialTypeAnalysisUtilities.getSpecialType(ie.getRight()) != null)) {
                token = "typeof";
              } else {
                token = "sheq";
              }
            }
            else if ((ie.getType() == 12) || (ie.getType() == 13)) {
              if ((SpecialTypeAnalysisUtilities.getSpecialType(ie.getLeft()) != null) || 
                (SpecialTypeAnalysisUtilities.getSpecialType(ie.getRight()) != null)) {
                token = "typeof";
              } else {
                token = "eq";
              }
            }
        }
        keyword = null;
        keyword = new KeywordUse(type, context, token, changeType);
        if (keyword != null) {
            this.increment(keyword);
        }
    }

    private void increment(KeywordUse keyword) {
        Integer count = this.keywordMap.get((Object)keyword);
        count = count == null ? 1 : count + 1;
        this.keywordMap.put(keyword, count);
    }
}

