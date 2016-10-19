package data.schema;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * Classe che modella lo schema del network
 */
public class Schema{
	
	/**
	 * lista  di attributi
	 */
	private List<Attribute> attributes;
	
	/**
	 * attributo target
	 */
	private ContinuousAttribute target;
	
	/**
	 * attributo identificativo
	 */
	private Attribute id;
	
	/**
	 * lista degli attributi aggregati
	 */
	private List<CollectiveAttribute> collectiveAttributes;
	
	/**
	 * 
	 * @param attributes lista di attributi descrittivi e collettivi
	 * 
	 * avvalora attributes
	 */
	public Schema(List<Attribute> attr, List<CollectiveAttribute> collectiveAttr){
		this.attributes = attr;
		this.collectiveAttributes = collectiveAttr;
	}
	
	
	
	/**
	 * 
	 * @param attributes lista di attributi descrittivi
	 * 
	 * avvalora attributes
	 */
	public Schema(List<Attribute> attr){
		this.attributes = attr;
		this.collectiveAttributes = new LinkedList<CollectiveAttribute>();
	}
	/**
	 * 
	 * @param index indice dell'attributo
	 * @return l'attributo con indice index
	 */
	public Attribute getAttribute(int index){
		if(index<1 || index>this.attributes.size())
			return null;
		return this.attributes.get(index-1);
	}
	
	/**
	 * 
	 * @param index indice dell'attributo collettivo
	 * @return l'attributo collettivo con indice index
	 */
	public CollectiveAttribute getCollectiveAttribute(int index){
		if(index<1 || index>this.collectiveAttributes.size())
			return null;
		return this.collectiveAttributes.get(index-1);
	}
	
	/**
	 * 
	 * @param t attributo target
	 * avvalora target
	 */
	public void setTarget(ContinuousAttribute t){
		this.target = t;
	}
	
	/**
	 * 
	 * @return attributo target
	 */
	public ContinuousAttribute getTarget(){
		return this.target;
	}
	
	/**
	 * 
	 * @param id attributo identificativo
	 * avvalora id
	 */
	public void setId(Attribute id){
		this.id = id;
	}
	
	/**
	 * 
	 * @return attributo identificativo
	 */
	public Attribute getId(){
		return this.id;
	}
	
	/**
	 * 
	 * @return iteratore sulla lista di attributi
	 * 
	 * Itera solo sugli attributi validi 
	 */
	public Iterator<Attribute> getIteratorExplanatory(){
		final boolean validAttr[] = new boolean[attributes.size()];
		int j=0;
		
		for(int i=0;i<attributes.size();i++){
			Attribute current = attributes.get(i);
			if(current.isNeglected() || current.isNodeId() || current.isTarget())
				validAttr[i]=false;
			else 
				{validAttr[i]=true; j++;}
		}
		
		final int numValid = j;
		
		Iterator<Attribute> it = new Iterator<Attribute>(){

			int i=0;//numero attributi validi
			int index=0;//indice in validAttr
						
			@Override
			public boolean hasNext() {
				return i < numValid;
			}

			@Override
			public Attribute next() {
				while(index<validAttr.length){
					if(!validAttr[index])
						index++;
					else{
						i++;index++;
						return attributes.get(index-1);
						}
				}
				return null;
			}

			@Override
			public void remove() {
				for(int j=index-1;j>=0;j--){
					if(validAttr[j]){
						index=j;
						i--;
						break;
					}
				}
			}
		};
		
		return it;
	}

	/**
	 * 
	 * @return iteratore sulla lista di attributi collettivi
	 * 
	 */
	public Iterator<CollectiveAttribute> getCollectiveAttrIterator(){
		return this.collectiveAttributes.iterator();
	}

	/**
	 * 
	 * @return numero di attributi
	 */
	public int numberOfAttributes() {
		return attributes.size();
	}

	
	public int numberOfValidAttributes(){
		int n=0;
		for(Attribute current:attributes){
			if(current.isNeglected() || current.isNodeId() || current.isTarget())
				continue;
			n++;
		}
		return n;
	}
	/**
	 * 
	 * @return numero di attributi collettivi
	 */
	public int numberOfCollectiveAttributes() {
		return collectiveAttributes.size();
	}
	
	
	public void setCollectiveAttributes(String fileName) throws IOException,ClassNotFoundException {
		
		FileInputStream inFile= new FileInputStream(fileName+".collective");
		ObjectInputStream inStream =new ObjectInputStream(inFile);
		
		collectiveAttributes=(List<CollectiveAttribute>)(inStream.readObject());
		inStream.close();
		
		
	}
	

	
	public void setCollectiveAttributes(List<CollectiveAttribute> colAttributes) {
		this.collectiveAttributes=colAttributes;
		
	}
	
	public void serializeCollectiveAttributeSchema(String filename) throws IOException{
		filename=filename.replace(".","_");
		FileOutputStream outFile = new FileOutputStream(filename+".collective");
		ObjectOutputStream outStream = new ObjectOutputStream(outFile);
		outStream.writeObject(collectiveAttributes);
		outStream.close();
	}

	
	public String printCollectiveSchema(){
		return collectiveAttributes.toString();
	}
	
	public String printDescriptiveSchema(){
		return attributes.toString();
	}
}
