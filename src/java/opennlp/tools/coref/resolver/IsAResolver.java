///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2003 Thomas Morton
//
//This library is free software; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public
//License as published by the Free Software Foundation; either
//version 2.1 of the License, or (at your option) any later version.
//
//This library is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//////////////////////////////////////////////////////////////////////////////
package opennlp.tools.coref.resolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import opennlp.tools.coref.DiscourseEntity;
import opennlp.tools.coref.mention.MentionContext;

public class IsAResolver extends MaxentResolver {

  Pattern predicativePattern;

  public IsAResolver(String projectName, ResolverMode m) throws IOException {
    super(projectName, "/imodel", m, 20);
    showExclusions = false;
    //predicativePattern = Pattern.compile("^(,|am|are|is|was|were|--)$");
    predicativePattern = Pattern.compile("^(,|--)$");
  }
  
  public IsAResolver(String projectName, ResolverMode m, NonReferentialResolver nrr) throws IOException {
    super(projectName, "/imodel", m, 20,nrr);
    showExclusions = false;
    //predicativePattern = Pattern.compile("^(,|am|are|is|was|were|--)$");
    predicativePattern = Pattern.compile("^(,|--)$");
  }


  public boolean canResolve(MentionContext ec) {
    if (ec.getHeadTokenTag().startsWith("NN")) {
      return (ec.getPreviousToken() != null && predicativePattern.matcher(ec.getPreviousToken().toString()).matches());
    }
    return false;
  }

  protected boolean excluded(MentionContext ec, DiscourseEntity de) {
    MentionContext cec = de.getLastExtent();
    //System.err.println("IsAResolver.excluded?: ec.span="+ec.getSpan()+" cec.span="+cec.getSpan()+" cec="+cec.toText()+" lastToken="+ec.getNextToken());
    if (ec.getSentenceNumber() != cec.getSentenceNumber()) {
      //System.err.println("IsAResolver.excluded: (true) not same sentence");
      return (true);
    }
    //shallow parse appositives
    //System.err.println("IsAResolver.excluded: ec="+ec.toText()+" "+ec.span+" cec="+cec.toText()+" "+cec.span);
    if (cec.getSpan().getEnd() == ec.getSpan().getStart() - 2) {
      return (false);
    }
    //full parse w/o trailing comma
    if (cec.getSpan().getEnd() == ec.getSpan().getEnd()) {
      //System.err.println("IsAResolver.excluded: (false) spans share end");
      return (false);
    }
    //full parse w/ trailing comma or period
    if (cec.getSpan().getEnd() <= ec.getSpan().getEnd() + 2 && (ec.getNextToken().toString().equals(",") || ec.getNextToken().toString().equals("."))) {
      //System.err.println("IsAResolver.excluded: (false) spans end + punct");
      return (false);
    }
    //System.err.println("IsAResolver.excluded: (true) default");
    return (true);
  }

  protected boolean outOfRange(MentionContext ec, DiscourseEntity de) {
    MentionContext cec = de.getLastExtent();
    return (cec.getSentenceNumber() != ec.getSentenceNumber());
  }

  protected boolean diffCriteria(DiscourseEntity de) {
    return (true);
  }

  protected List getFeatures(MentionContext mention, DiscourseEntity entity) {
    List features = new ArrayList();
    features.addAll(super.getFeatures(mention, entity));
    if (entity != null) {
      MentionContext ant = entity.getLastExtent();
      List leftContexts = getContextFeatures(ant);
      for (int ci = 0, cn = leftContexts.size(); ci < cn; ci++) {
        features.add("l" + leftContexts.get(ci));
      }
      List rightContexts = getContextFeatures(mention);
      for (int ci = 0, cn = rightContexts.size(); ci < cn; ci++) {
        features.add("r" + rightContexts.get(ci));
      }
      features.add("hts"+ant.getHeadTokenTag()+","+mention.getHeadTokenTag());
    }
    /*
    if (entity != null) {
      //System.err.println("MaxentIsResolver.getFeatures: ["+ec2.toText()+"] -> ["+de.getLastExtent().toText()+"]");
      //previous word and tag
      if (ant.prevToken != null) {
        features.add("pw=" + ant.prevToken);
        features.add("pt=" + ant.prevToken.getSyntacticType());
      }
      else {
        features.add("pw=<none>");
        features.add("pt=<none>");
      }

      //next word and tag
      if (mention.nextToken != null) {
        features.add("nw=" + mention.nextToken);
        features.add("nt=" + mention.nextToken.getSyntacticType());
      }
      else {
        features.add("nw=<none>");
        features.add("nt=<none>");
      }

      //modifier word and tag for c1
      int i = 0;
      List c1toks = ant.tokens;
      for (; i < ant.headTokenIndex; i++) {
        features.add("mw=" + c1toks.get(i));
        features.add("mt=" + ((Parse) c1toks.get(i)).getSyntacticType());
      }
      //head word and tag for c1
      features.add("mh=" + c1toks.get(i));
      features.add("mt=" + ((Parse) c1toks.get(i)).getSyntacticType());

      //modifier word and tag for c2
      i = 0;
      List c2toks = mention.tokens;
      for (; i < mention.headTokenIndex; i++) {
        features.add("mw=" + c2toks.get(i));
        features.add("mt=" + ((Parse) c2toks.get(i)).getSyntacticType());
      }
      //head word and tag for n2
      features.add("mh=" + c2toks.get(i));
      features.add("mt=" + ((Parse) c2toks.get(i)).getSyntacticType());

      //word/tag pairs
      for (i = 0; i < ant.headTokenIndex; i++) {
        for (int j = 0; j < mention.headTokenIndex; j++) {
          features.add("w=" + c1toks.get(i) + "|" + "w=" + c2toks.get(j));
          features.add("w=" + c1toks.get(i) + "|" + "t=" + ((Parse) c2toks.get(j)).getSyntacticType());
          features.add("t=" + ((Parse) c1toks.get(i)).getSyntacticType() + "|" + "w=" + c2toks.get(j));
          features.add("t=" + ((Parse) c1toks.get(i)).getSyntacticType() + "|" + "t=" + ((Parse) c2toks.get(j)).getSyntacticType());
        }
      }
      features.add("ht=" + ant.headTokenTag + "|" + "ht=" + mention.headTokenTag);
      features.add("ht1=" + ant.headTokenTag);
      features.add("ht2=" + mention.headTokenTag);
     */
      //semantic categories
      /*
      if (ant.neType != null) {
        if (re.neType != null) {
          features.add("sc="+ant.neType+","+re.neType);
        }
        else if (!re.headTokenTag.startsWith("NNP") && re.headTokenTag.startsWith("NN")) {
          Set synsets = re.synsets;
          for (Iterator si=synsets.iterator();si.hasNext();) {
            features.add("sc="+ant.neType+","+si.next());
          }
        }
      }
      else if (!ant.headTokenTag.startsWith("NNP") && ant.headTokenTag.startsWith("NN")) {
        if (re.neType != null) {
          Set synsets = ant.synsets;
          for (Iterator si=synsets.iterator();si.hasNext();) {
            features.add("sc="+re.neType+","+si.next());
          }
        }
        else if (!re.headTokenTag.startsWith("NNP") && re.headTokenTag.startsWith("NN")) {
          //System.err.println("MaxentIsaResolover.getFeatures: both common re="+re.parse+" ant="+ant.parse);
          Set synsets1 = ant.synsets;
          Set synsets2 = re.synsets;
          for (Iterator si=synsets1.iterator();si.hasNext();) {
            Object synset = si.next();
            if (synsets2.contains(synset)) {
              features.add("sc="+synset);
            }
          }
        }
      }
    }
    */
    //System.err.println("MaxentIsResolver.getFeatures: "+features.toString());
    return (features);
  }
}